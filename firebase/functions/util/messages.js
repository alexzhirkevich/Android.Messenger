const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {notifyUsers,getUser} = require("./users");
const {redirectToBot} = require("./bots");
const {CHATS,MESSAGES,USERS,TIME,CREATOR_ID,CHAT_ID,ID,BOT,USER1,USER2,SENDER_ID} = require("../constants");

exports.updateLastMessage=async function(msgData, chatId, auth,isNew = false){
    if (msgData && chatId){
        try{
            const doc = admin.firestore().collection(CHATS).doc(chatId)
            let msgTime = TIME in msgData ? msgData.time : Date.now()
            try {
                const chatInfo = ((await doc.get()).data())
                if (chatInfo) {
                    if (isNew) {
                        msgTime = Date.now()
                        await doc.collection(MESSAGES).doc(msgData.id).update({
                            'time': msgTime,
                            'isPrivate': CREATOR_ID in chatInfo,
                            'senderId' : auth.uid,
                            'chatId' : chatId
                        })
                    }
                }
            }catch (e) {functions.logger.error(e)}

            if (!isNew)
                return await doc.update(
                    {
                        'lastMessageId': msgData.id,
                        'lastMessageTime': msgTime
                    })
            else {

                const snapshots = await Promise.all([
                    admin.firestore().collection(CHATS).doc(msgData.chatId).get(),
                    admin.firestore().collection(USERS).doc(msgData.senderId).get(),
                    admin.firestore().collection(CHATS).doc(msgData.chatId).collection(USERS).get()])

                const chatData = snapshots[0].data()
                const senderData = snapshots[1].data()

                if (chatData && senderData){

                    const payload = {
                        notification: {
                            title: senderData.name,
                            body: msgData.text,
                            icon: chatData.imageUri
                        }
                    };

                    const ids = snapshots[2].docs.map((doc) => {
                        if (doc.data())
                            return doc.data().id
                        else
                            return null
                    })

                    return await notifyUsers(ids, payload)
                }
            }
        }catch(e){
            return functions.logger.error(e.message)
        }
    }
    return null
}

exports.replaceDeletedMessage=async function (msgData,chatId,auth){
    if (msgData) {
        try {
            const snapshot = await admin.firestore().collection(CHATS).doc(msgData.chatId).collection(MESSAGES)
                .orderBy(TIME).limitToLast(1).get()
            return await exports.updateLastMessage(snapshot.docs[0].data(),chatId,auth,false)
        }catch (e){
            return functions.logger.error(e.message)
        }
    }
    return null
}

exports.checkForBotMessage = async function(msgData){
    if (msgData && msgData[id] && msgData[CHAT_ID]){

        const chat = await admin.firestore().collection(CHATS).doc(msgData[CHAT_ID]).get()

        if (chat[USER1] && chat[USER2]) {

            const user1 = await getUser(chat[USER1])
            const user2 = await getUser(chat[USER2])

            const promises = []

            if (user1[BOT] && user1[BOT] === true && user1[ID] && user2[ID] &&
                msgData[ID] && msgData[SENDER_ID]===user2[ID]) {
                promises.push(redirectToBot(msgData[ID],user1[ID]))
            }

            if (user2[BOT] && user2[BOT] === true && user1[ID] && user2[ID] &&
                msgData[ID] && msgData[SENDER_ID]===user1[ID]) {
                promises.push(redirectToBot(msgData[ID],user2[ID]))
            }

            return promises.length === 0 ? null : promises
        }
    } else return null
}

