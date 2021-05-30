const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {CHANNELS,ADMINS,USERS} = require("../constants");

exports.createChannel = async function(channel) {
    if (channel) {
        const doc = admin.firestore().collection(CHANNELS).doc(channel.id)

        await Promise.all([doc.collection(ADMINS).doc(channel.creatorId).set({
            'id': channel.creatorId,
            'canPost': true,
            'canDelete': true,
            'canEdit': true,
            'canBan': true
        }),
            exports.joinChannel(channel.creatorId, channel.id)])
    }
}

exports.deleteChannel = async function(channelId){

    if (channelId) {
        try {
            const chatDoc = admin.firestore().collection(CHANNELS).doc(channelId)

            const userIds = (await chatDoc.collection(USERS).get()).docs.map(doc => doc.id)

            userIds.forEach((id) => {
                try {
                    leaveChannel(id, channelId)
                } catch (e) {
                    functions.logger.error(e)
                }
            })

            return await admin.firestore().collection(CHANNELS).doc(channelId).delete()
        }catch (e){
            return functions.logger.error(e)
        }
    }
    return null
}

exports.joinChannel = async  function (userId,channelId) {
    if (userId && channelId) {
        return await admin.firestore().collection(CHANNELS).doc(channelId).collection(USERS).doc(userId)
            .set({
                "allowNotifications": true
            })
    }
    return null
}

exports.leaveChannel = async function(userId,channelId) {
    if (userId && channelId)
    {
        try {
            let channelDoc = admin.firestore().collection(CHANNELS).doc(channelId)
            await channelDoc.collection(ADMINS).doc(userId).delete()
            return await channelDoc.collection(USERS).doc(userId).delete()
        } catch (e){
            return functions.logger.error(e)
        }
    }
    return null
}