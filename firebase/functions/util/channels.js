const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {ID, CHANNELS,ADMINS,USERS,CREATOR_ID,PRIVATE,PUBLIC,SUBSCRIBERS: SUB_COUNT} = require("../constants");

exports.createChannel = async function(channel) {
    if (channel && channel[ID] && channel[CREATOR_ID]) {
        const channelDoc = admin.firestore().collection(CHANNELS).doc(channel[ID])
        const userDoc = admin.firestore().collection(USERS).doc(channel[CREATOR_ID])

        return await Promise.all(
            [
                channelDoc.collection(ADMINS).doc(channel[CREATOR_ID]).set(
                    {
                        'id' : channel[CREATOR_ID],
                        'canPost': true,
                        'canDelete': true,
                        'canEdit': true,
                        'canBan': true
                    }
                ),
                userDoc.collection(CHANNELS).doc(channel[ID]).set(
                        {
                            'lastPostTime': 0
                        }
                    ),
            ]
        )
    } else return null
}

exports.deleteChannel = async function(channelId){

    if (channelId) {
        try {
            const channelDoc = admin.firestore().collection(CHANNELS).doc(channelId)

            const userIds = (await channelDoc.collection(USERS).get()).docs.map(doc => doc.id)

            userIds.forEach((id) => {
                try {
                    exports.leaveChannel(id, channelId)
                } catch (e) {
                    functions.logger.error(e)
                }
            })

            return await channelDoc.delete()
        }catch (e){
            return functions.logger.error(e)
        }
    }
    return null
}

exports.changeSubCount = async function(channelId,count){

    const shapshot = await admin.firestore().collection(CHANNELS).doc(channelId).get()

    return shapshot.exists ? await admin.firestore().collection(CHANNELS).doc(channelId).collection(PRIVATE).doc(PUBLIC).set(
        { 'subscribers' : admin.firestore.FieldValue.increment(count)},{merge : true}
    ) : null
}

exports.joinChannel = async function (userId,channelId) {
    if (userId && channelId) {

        const shapshot = await admin.firestore().collection(CHANNELS).doc(channelId).get()

        return shapshot.exists ? await Promise.all(
            [
                admin.firestore().collection(CHANNELS).doc(channelId).collection(USERS).doc(userId)
                    .set(
                        {
                            'allowNotifications': true
                        }
                    ), 
                exports.changeSubCount(channelId,1)
            ]
        ) : null
    } 
    return null
}



exports.leaveChannel = async function(userId,channelId) {
    if (userId && channelId)
    {
        try {
            let channelDoc = admin.firestore().collection(CHANNELS).doc(channelId)
            const snapshot = await channelDoc.get()
            return snapshot.exists ? Promise.all(
                [
                    channelDoc.collection(ADMINS).doc(userId).delete(),
                    channelDoc.collection(USERS).doc(userId).delete(),
                    exports.changeSubCount(channelId,-1)  
                ]
            ) : null
        } catch (e){
            return functions.logger.error(e)
        }
    }
    return null
}