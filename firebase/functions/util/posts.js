const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {notifyUsers} = require("./users");
const {CHANNELS,POSTS,USERS,TIME} = require("../constants");

exports.createPost = async function(postData){
    if (postData) {
        postData.time = Date.now()
        return exports.updateLastPost(postData)
    }
    return null
}

exports.updateLastPost=async function(postData,notify = true){
    if (postData){
        try {

            const doc = admin.firestore().collection(CHANNELS).doc(postData.channelId)

            await doc.update({
                'lastPostId': postData.id,
                'lastPostTime': postData.time
            })

            if (notify){

                const snapshots = await Promise.all([
                    admin.firestore().collection(CHANNELS).doc(postData.channelId).get(),
                    admin.firestore().collection(CHANNELS).doc(postData.channelId).collection(USERS).get()]
                )

                const channelData = snapshots[0].data()

                if (channelData){

                    const payload = {
                        notification: {
                            title: channelData.name,
                            body: postData.text,
                            icon: channelData.imageUri
                        }
                    };

                    const ids = snapshots[1].docs.map((doc) => {
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

exports.replaceDeletedPost = async function(postData){
    if (postData) {
        try {
            const snapshot = await admin.firestore().collection(CHANNELS).doc(postData.channelId).collection(POSTS)
                .orderBy(TIME).limitToLast(1).get()
            return await exports.updateLastPost(snapshot.docs[0].data(),false)
        }catch (e){
            return functions.logger.error(e.message)
        }
    }
    return null
}