package com.alexz.messenger.app.data.repo

import com.alexz.messenger.app.util.FirebaseUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object UserListRepository {
    @JvmStatic
    fun getUsers(chatID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(chatID)
                .child(FirebaseUtil.USERS)
    }

    @JvmStatic
    fun getUser(userID: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(userID)
                .child(FirebaseUtil.INFO)
    }
}