const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {CHATS,USERS} = require("../constants");

exports.joinChat = async function(userId,chatId){

    if (userId && chatId) {
        return await admin.firestore().collection(CHATS).doc(chatId).collection(USERS).doc(userId)
            .set({
                "allowNotifications": true
            })
    }
    return null
}

exports.leaveChat =async function(userId,chatId){
    if (userId && chatId) {
        return await admin.firestore().collection(CHATS).doc(chatId).collection(USERS).doc(userId)
            .delete()
    }
    return null
}

exports.deleteChat= async function(chatId){
    if (chatId) {
        try {
            const chatDoc = admin.firestore().collection(CHATS).doc(chatId)

            const userIds = (await chatDoc.collection(USERS).get()).docs.map(doc => doc.id)

            userIds.forEach((id) => {
                try {
                    exports.leaveChat(id, chatId)
                } catch (e) {
                    functions.logger.error(e)
                }
            })

            return await chatDoc.delete()
        }catch (e){
            return functions.logger.error(e)
        }
    }
    return null
}
