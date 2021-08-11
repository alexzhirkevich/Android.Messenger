const functions = require('firebase-functions')
const admin = require('firebase-admin')
const {PHONE, ID} = require("../constants");
const {deleteChannel} = require("./channels");
const {deleteChat} = require("./chats");
const {USERS,CHATS,CHANNELS,BOT,CREATION_TIME,USERNAME,USERNAME_SEARCH} = require("../constants");

exports.notifyUsers=async function(ids,payload) {
    if (ids && payload) {
        try {
            return await Promise.all(ids.map((id) => {
                try {
                    return id ? admin.firestore().collection(USERS).doc(id).get()
                        .then((snap) => {
                            try {
                                let data = snap.data()
                                if (data != null) {
                                    return admin.messaging().sendToDevice(data.notificationToken, payload)
                                }
                            } catch (ignore) {}
                        }) : Promise.resolve()
                } catch (e) { return Promise.resolve() }
            }))
        } catch (e) {
            return functions.logger.error(e.message)
        }
    }
    return null
}

exports.addUserToDatabase=async function(user){
    if (user){
        let userName = user.displayName
        if (userName == null) {
            userName = "User" + Date.now()
        }

        const serverTime = Date.now()

        return await admin.firestore().collection(USERS).doc(user.uid).set({
            'id': user.uid,
            'online': true,
            'phone' : user.phoneNumber,
            'name': "User" + serverTime,
            'creationTime' : serverTime,
            'imageUri' : ""
        },{merge:true})
    }
    return null
}

exports.getUser = async function(id){
    return admin.firestore().collection(USERS).doc(id).get()
}

exports.deleteUser=async function(user) {
    if (user) {
        try {
            let userDoc = admin.firestore().collection(USERS).doc(user.uid)
            let chatsData = (await userDoc.collection(CHATS).get()).docs.map(d => d.data())
            let channelsData = (await userDoc.collection(CHANNELS).get()).docs.map(d => d.data())

            chatsData
                .filter(data => data && data.name.includes(user.uid))
                .map(data => data.id)
                .forEach(id => {
                    try {
                        deleteChat(id)
                    }catch (e) {
                        functions.logger.error(e)
                    }
                })

            channelsData
                .filter(data => data && data.name.includes(user.uid))
                .map(data => data.id)
                .forEach(id => {
                    try{
                        deleteChannel(id)
                    }catch (e){
                        functions.logger.error(e)
                    }
                })

            return await admin.firestore().collection(USERS).doc(user.uid).delete()
        } catch (e) {
            return functions.logger.error(e)
        }
    }
    return null
}

exports.updateUser = async function(before,after){
    if (before && after){
        after[ID] = before[ID]
        after[PHONE] = before[PHONE]
        after[BOT] = before[BOT]
        after[CREATION_TIME] = before[CREATION_TIME]
        after[USERNAME_SEARCH] = after[USERNAME].toString().toLowerCase() 
        return await admin.firestore().collection(USERS).doc(before[ID]).set(after,{merge = true})
    }
    return null
}