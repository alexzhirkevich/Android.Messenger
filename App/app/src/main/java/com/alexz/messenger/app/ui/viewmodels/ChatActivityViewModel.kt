package com.alexz.messenger.app.ui.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.ChatApplication.Companion.AppContext
import com.alexz.messenger.app.data.entities.imp.Message
import com.alexz.messenger.app.data.providers.imp.FirebaseStorageProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreMessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.StorageProvider
import com.alexz.messenger.app.util.FirebaseUtil

class ChatActivityViewModel(
        private val messagesProvider: MessagesProvider = FirestoreMessagesProvider(),
        private val storageProvider: StorageProvider = FirebaseStorageProvider(),
        private val chatsProvider: ChatsProvider =  FirestoreChatsProvider()
) : ViewModel(){

    fun getChat(chatId:String) = chatsProvider.get(chatId,null)

    fun sendMessage(message: Message) = messagesProvider.create(message)

    fun uploadImage(uri: Uri) = storageProvider.uploadImage(uri)
    fun uploadVoice (uri: Uri) = storageProvider.uploadVoice(uri)

    fun deleteMessage(message: Message) = messagesProvider.delete(message)

    fun copyInviteLink(chatId: String) {
        val cd = ClipData.newPlainText("fm-chat-invite", FirebaseUtil.createChatInviteLink(chatId))
        getSystemService(AppContext, ClipboardManager::class.java)?.apply {
            setPrimaryClip(cd)
        }
    }

    fun copyMessage( msg: Message) {
        val cd = ClipData.newPlainText("Msg from " + msg.senderId, msg.text)
        getSystemService(AppContext,ClipboardManager::class.java)?.apply {
            setPrimaryClip(cd)
        }
    }
}