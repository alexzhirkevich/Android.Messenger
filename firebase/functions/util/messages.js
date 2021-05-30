const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {notifyUsers} = require("./users");
const {CHATS,MESSAGES,USERS,TIME} = require("../constants");

exports.updateLastMessage=async function(msgData,notify = true){
    if (msgData){
        try{

            const doc = admin.firestore().collection(CHATS).doc(msgData.chatId)
            const serverTime = Date.now()

            await doc.collection(MESSAGES).doc(msgData.id).update({
                'time' : serverTime
            })

            await doc.update({
                'lastMessageId': msgData.id,
                'lastMessageTime': serverTime
            })

            if (notify) {

                const snapshots = await Promise.all([
                    admin.firestore().collection(CHATS).doc(msgData.chatId).get(),
                    admin.firestore().collection(USERS).doc(msgData.senderId).get(),
                    admin.firestore().collection(CHATS).doc(msgData.chatId).collection(USERS).get()]
                )

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

                    await notifyUsers(ids, payload)
                }
            }
        }catch(e){
            return functions.logger.error(e.message)
        }
    }
    return null
}

exports.replaceDeletedMessage=async function (msgData){
    if (msgData) {
        try {
            const snapshot = await admin.firestore().collection(CHATS).doc(msgData.chatId).collection(MESSAGES)
                .orderBy(TIME).limitToLast(1).get()
            return await exports.updateLastMessage(snapshot.docs[0].data(),false)
        }catch (e){
            return functions.logger.error(e.message)
        }
    }
    return null
}

