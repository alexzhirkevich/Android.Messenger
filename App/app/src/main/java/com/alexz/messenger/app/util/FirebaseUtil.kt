package com.alexz.messenger.app.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

object FirebaseUtil {
    val CREATOR_ID = "creatorId"
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

    const val URL_BASE = "https://firemessenger.com/"
    const val LINK_CHANNEL = "joinchannel/"
    const val LINK_CHAT = "joinchat/"

    val channelsCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(CHANNELS)

    val usersCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(USERS)

    val chatsCollection : CollectionReference
        get() = FirebaseFirestore.getInstance().collection(CHATS)

    val currentFireUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    fun setOnline(online: Boolean) {
        if (FirebaseAuth.getInstance().uid != null) {
            val userInfoRef = FirebaseDatabase.getInstance().reference
                    .child(USERS)
                    .child(currentFireUser!!.uid)
                    .child(INFO)
            userInfoRef.child(ONLINE).setValue(online)
            if (!online) {
                userInfoRef.child(LAST_ONLINE).setValue(Date().time)
            }
        }
    }

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