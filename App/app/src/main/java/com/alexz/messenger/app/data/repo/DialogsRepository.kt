package com.alexz.messenger.app.data.repo

import android.util.Log
import com.alexz.messenger.app.data.model.imp.Chat
import com.alexz.messenger.app.data.model.result.Error
import com.alexz.messenger.app.data.model.result.Future
import com.alexz.messenger.app.data.model.result.MutableFuture
import com.alexz.messenger.app.data.model.result.Success
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.messenger.app.BuildConfig
import com.messenger.app.R

object DialogsRepository {
    @JvmStatic
    fun createChat(d: Chat) {
        val userId = FirebaseUtil.getCurrentUser().id
        var chatRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .push()
        d.id = userId + ":" + chatRef.key
        d.creatorId = userId
        chatRef = chatRef.parent!!.child(d.id)
        chatRef.child(FirebaseUtil.INFO).setValue(d)
        chatRef.child(FirebaseUtil.USERS)
                .child(userId).setValue("")
        FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(d.creatorId)
                .child(FirebaseUtil.CHATS)
                .child(d.id)
                .setValue("")
    }

    @JvmStatic
    fun findChat(chatId: String?): Future<Chat> {
        val future = MutableFuture<Chat>()
        val id = FirebaseUtil.getCurrentUser().id
        val addRef = FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(id)
                .child(FirebaseUtil.CHATS)
                .child(chatId!!)
        addRef.setValue("")
                .addOnSuccessListener { aVoid: Void? ->
                    getChat(chatId).get()
                            .addOnSuccessListener { snapshot: DataSnapshot ->
                                if (snapshot.exists()) {
                                    future.post(Success(snapshot.getValue(Chat::class.java)))
                                    FirebaseDatabase.getInstance().reference
                                            .child(FirebaseUtil.CHATS)
                                            .child(chatId)
                                            .child(FirebaseUtil.USERS)
                                            .child(FirebaseUtil.getCurrentUser().id)
                                            .setValue("")
                                    if (BuildConfig.DEBUG) {
                                        Log.e("FIND CHAT", "SUCCESS: Chat added")
                                    }
                                } else {
                                    onChatFindFailure(addRef, future)
                                }
                            }
                            .addOnFailureListener { e: Exception? -> onChatFindFailure(addRef, future) }
                            .addOnCanceledListener { onChatFindFailure(addRef, future) }
                }
        return future
    }

    @JvmStatic
    fun removeEmptyChatId(chatId: String?) {
        FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(FirebaseUtil.getCurrentUser().id)
                .child(FirebaseUtil.CHATS)
                .child(chatId!!).setValue(null)
    }

    @JvmStatic
    val chatIds: DatabaseReference
        get() = FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.USERS)
                .child(FirebaseUtil.getCurrentUser().id)
                .child(FirebaseUtil.CHATS)

    @JvmStatic
    fun getChat(chatId: String?): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(chatId!!)
                .child(FirebaseUtil.INFO)
    }

    @JvmStatic
    fun removeChat(chat: Chat) {
        val userId = FirebaseUtil.getCurrentUser().id
        if (chat.creatorId == userId) {
            FirebaseDatabase.getInstance().reference
                    .child(FirebaseUtil.CHATS)
                    .child(chat.id).removeValue()
        } else {
            FirebaseDatabase.getInstance().reference
                    .child(FirebaseUtil.USERS).child(userId).child(FirebaseUtil.CHATS).child(chat.id).setValue(null)
        }
    }

    private fun onChatFindFailure(addRef: DatabaseReference, future: MutableFuture<Chat>) {
        addRef.removeValue()
        future.post(Error(R.string.error_chat_not_found))
        if (BuildConfig.DEBUG) {
            Log.e("FIND CHAT", "FAILURE: Chat added")
        }
    }
}