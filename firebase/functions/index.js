const functions = require('firebase-functions')
const admin = require('firebase-admin')
const express = require("express");

const {MESSAGES,POSTS,CHANNELS,USERS,CHATS,ONLINE,ID} = require("./constants");
const {joinChannel,leaveChannel,createChannel,deleteChannel} = require("./util/channels");
const {createPost,replaceDeletedPost} = require("./util/posts");
const {updateLastMessage,replaceDeletedMessage} = require("./util/messages");
const {addUserToDatabase,deleteUser} = require("./util/users");
const {deleteChat,leaveChat,joinChat} = require("./util/chats");

admin.initializeApp()


// exports._joinChannel = functions.https.onRequest(async (req,res)=> {
//
//     const token = req.params.token
//     const userId = req.params.userId
//     const channelId = req.params.chatId
//     if (token && userId && channelId)
//
//     admin.auth().verifyIdToken(token).then((async decodedToken => {
//         if (decodedToken.uid === userId) {
//
//             if (await joinChannel(userId, channelId)){
//                 res.status(200).send(true)
//             }
//         }
//     })).catch((reason => res.status(404).send(false)))
// })

exports.onUserOnlineChanged = functions.database.ref('/users/{id}/online')

    .onWrite((snapshot,context) => {

        functions.logger.debug(snapshot.after.val())

        const val = snapshot.after.val()

        const online = val ? val : false

        functions.logger.debug({'USER' : context.params.id,'ONLINE' : online})

        return admin.firestore().collection(USERS).doc(context.params.id).set(
            {
                'online' : online,
                'lastOnline' : Date.now()
            },
            {
                merge : true
            }
        )
        
    })

//***********************CHATS***********************

exports.onChatCreated = functions.firestore
    .document(`${CHATS}/{chatId}`)
    .onCreate(((snapshot) => {
        const data = snapshot.data()
        if (data){
            return joinChat(data.creatorId,data.id)
        }
    }))

exports.onChatJoined = functions.firestore
    .document(`${USERS}/{userId}/${CHATS}/{chatId}`)
    .onCreate((snapshot,context) => {
        let { userId, chatId } = context.params
        return joinChat(userId,chatId)
    })

exports.onChatLeaved = functions.firestore
    .document(`${USERS}/{userId}/${CHATS}/{chatId}`)
    .onDelete((snapshot,context) => {
        let { userId, chatId } = context.params
        return leaveChat(userId,chatId)
    })

exports.onChatDeleted = functions.firestore
    .document(`${CHATS}/{chatId}`)
    .onDelete(((snapshot) => {
        const data = snapshot.data()
        if (data){
            return deleteChat(data.id)
        }
    }))

//***********************CHATS***********************

//**********************CHANNELS**********************

exports.onChannelJoined = functions.firestore
    .document(`${USERS}/{userId}/${CHANNELS}/{channelId}`)
    .onCreate((snapshot,context) => {
        let { userId, channelId } = context.params
        return joinChannel(userId,channelId)
    })

exports.onChannelLeaved = functions.firestore
    .document(`${USERS}/{userId}/${CHANNELS}/{channelId}`)
    .onCreate((snapshot,context) => {
        let { userId, channelId } = context.params
        return leaveChannel(userId,channelId)
    })

exports.onChannelCreated = functions.firestore
    .document(`${CHANNELS}/{channelId}`)
    .onCreate(((snapshot) => {
        const data = snapshot.data()
        if (data){
            return createChannel(data.creatorId,data.id)
        }
    }))
exports.onChannelDeleted = functions.firestore
    .document(`${CHANNELS}/{channelId}`)
    .onDelete(((snapshot) => {
        const data = snapshot.data()
        if (data) {
            return deleteChannel(data.creatorId,data.id)
        }
    }))

//**********************CHANNELS**********************

//***********************POSTS***********************

exports.onPostCreated = functions.firestore
    .document(`${CHANNELS}/{channelId}/${POSTS}/{postId}`)
    .onCreate((snapshot) => createPost(snapshot.data()))

exports.onPostDeleted = functions.firestore
    .document(`${CHANNELS}/{channelId}/${POSTS}/{postId}`)
    .onDelete((snapshot) => replaceDeletedPost(snapshot.data()))

//***********************POSTS***********************

//**********************MESSAGES**********************


exports.onMessageChanged = functions.firestore
    .document(`${CHATS}/{chatId}/${MESSAGES}/{msgId}`)
    .onWrite((snapshot,context) => {
        if (context.authType === "USER") {
            const beforeData = snapshot.before.exists ? snapshot.before.data() : null
            const afterData = snapshot.after.exists ? snapshot.after.data() : null
            if (!beforeData) {
                updateLastMessage(afterData, context.params.chatId, context.auth, true)
            } else if (!afterData) {
                replaceDeletedMessage(beforeData, context.params.chatId,context.auth)
            } else {
                const data = {}
                if (beforeData.time !== afterData.time) {
                    data["time"] = beforeData.time.toLong(true)
                }
                if (beforeData.senderId !== afterData.senderId) {
                    data["senderId"] = beforeData.senderId
                }
                if (beforeData.chatId !== afterData.chatId) {
                    data["chatId"] = afterData.chatId
                }
                if (beforeData.isPrivate !== afterData.isPrivate) {
                    data["isPrivate"] = Boolean(afterData.isPrivate)
                }
            }
        }
    })


exports.onMessageCreated = functions.firestore
    .document(`${CHATS}/{chatId}/${MESSAGES}/{msgId}`)
    .onCreate((snapshot) => updateLastMessage(snapshot.data(),true,true))

exports.onMessageDeleted = functions.firestore
    .document(`${CHATS}/{chatId}/${MESSAGES}/{msgId}`)
    .onDelete((snapshot) => replaceDeletedMessage(snapshot.data()))

//**********************MESSAGES**********************

//***********************USERS***********************

exports.onUserRegistered = functions.auth.user()
    .onCreate((user) => addUserToDatabase(user))

exports.onUserDeleted = functions.auth.user()
    .onDelete((user) => deleteUser(user))

//***********************USERS***********************