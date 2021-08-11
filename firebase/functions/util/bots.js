const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {ID,CHAT_ID,BOTS,BOTS_PRIVATE} = require('../constants')

exports.redirectToBot = async function(msgData,botId){
    if(msgData && msgData[ID] && msgData[CHAT_ID] && botId) {
        const key = (await admin.firestore().collection(BOTS_PRIVATE).doc(botId).get()).key
        return admin.database().child(BOTS).child(key).child(msgData[ID]).set(msgData[CHAT_ID])
    } else return null
}