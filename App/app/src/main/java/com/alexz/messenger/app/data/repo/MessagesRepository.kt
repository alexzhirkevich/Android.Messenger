package com.alexz.messenger.app.data.repo

import com.alexz.messenger.app.data.model.imp.Message
import com.alexz.messenger.app.util.FirebaseUtil
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object MessagesRepository {
    fun getMessagesReference(chatId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(chatId)
                .child(FirebaseUtil.MESSAGES)
    }

    @JvmStatic
    fun getChatInfo(chatId: String): Task<DataSnapshot> {
        return FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(chatId)
                .child(FirebaseUtil.INFO)
                .get()
    }

    @JvmStatic
    fun sendMessage(m: Message, replaceText: String) {
        val ref = FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(m.chatId)
                .child(FirebaseUtil.MESSAGES).push()
        m.id = ref.key!!
        m.senderId = FirebaseUtil.getCurrentUser().id
        ref.setValue(m)
        if (m.text.isEmpty()) {
            m.text = replaceText
        }
        FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(m.chatId)
                .child(FirebaseUtil.INFO)
                .child(FirebaseUtil.LASTMESSAGE)
                .setValue(m)
    }

    @JvmStatic
    fun deleteMessage(item: Message) {
        FirebaseDatabase.getInstance().reference
                .child(FirebaseUtil.CHATS)
                .child(item.chatId)
                .child(FirebaseUtil.MESSAGES)
                .child(item.id)
                .removeValue()
    }
}