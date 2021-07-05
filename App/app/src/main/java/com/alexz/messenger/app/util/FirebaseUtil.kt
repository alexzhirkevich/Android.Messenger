package com.alexz.messenger.app.util

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtil {
    const val USERNAME = "username"
    const val CONTACTS = "contacts"
    const val PHONE = "phone"
    const val IMAGE_URI = "imageUri"
    const val DESCRIPTION = "description"
    const val CREATOR_ID = "creatorId"
    const val TIME= "time"
    const val MEDIA_MESSAGES = "media_messages"
    const val VOICE_MESSAGES = "voice_messages"
    const val ID = "id"
    const val NAME = "name"
    const val PROFILES = "profiles"
    const val ADMINS = "admins"
    const val SEARCH_NAME = "name"
    const val USERS = "users"
    const val CHATS = "chats"
    const val CHANNELS = "channels"
    const val POSTS = "posts"
    const val INFO = "info"
    const val MESSAGES = "messages"
    const val LAST_MESSAGE = "lastMessage"
    const val LAST_POST = "lastPost"
    const val ONLINE = "online"
    const val LAST_ONLINE = "lastOnline"
    const val VOICE_URI = "voiceUri"
    const val VOICE_LEN = "voiceLen"
    const val MEDIA_CONTENT = "mediaContent"
    const val USER_DATA = "userdata"
    const val IMAGES = "images"
    const val VOICES = "voices"
    const val DATA = "data"
    const val REFERENCE = "reference"
    const val NOTIFY_TOKEN = "notificationToken"
    const val PRIVATE_USERS_INFO = "privateUsersInfo"

    const val URL_BASE = "https://firemessenger.com/"
    const val LINK_CHANNEL = "joinchannel/"
    const val LINK_CHAT = "joinchat/"

    val channelsCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(CHANNELS)

    val usersCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(USERS)

    val privateUsersCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(PRIVATE_USERS_INFO)

    val phonesCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(PHONE)


    val chatsCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(CHATS)

    fun createChatInviteLink(id: String): CharSequence =
            buildString {
                append(URL_BASE)
                append(LINK_CHAT)
                append(id)
            }
    fun createChannelInviteLink(id: String): CharSequence =
            buildString {
                append(URL_BASE)
                append(LINK_CHANNEL)
                append(id)
            }
}