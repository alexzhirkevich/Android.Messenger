const functions = require('firebase-functions')
const admin = require('firebase-admin')

const {MESSAGES,POSTS,CHANNELS,USERS,CHATS} = require("./constants");
const {joinChannel,leaveChannel,createChannel,deleteChannel} = require("./util/channels");
const {createPost,replaceDeletedPost} = require("./util/posts");
const {updateLastMessage,replaceDeletedMessage} = require("./util/messages");
const {addUserToDatabase,deleteUser} = require("./util/users");
const {deleteChat,leaveChat,joinChat} = require("./util/chats");

admin.initializeApp()

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

exports.onMessageCreated = functions.firestore
    .document(`${CHATS}/{chatId}/${MESSAGES}/{msgId}`)
    .onCreate((snapshot) => updateLastMessage(snapshot.data()))

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