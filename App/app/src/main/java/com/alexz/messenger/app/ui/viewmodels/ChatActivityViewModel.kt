package com.alexz.messenger.app.ui.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.ChatApplication.Companion.AppContext
import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.imp.Message
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.StorageProvider
import com.alexz.messenger.app.data.repo.ChatsRepository
import com.alexz.messenger.app.data.repo.MessagesRepository
import com.alexz.messenger.app.data.repo.StorageRepository
import com.alexz.messenger.app.util.FirebaseUtil
import io.reactivex.rxjava3.core.Observable

class ChatActivityViewModel(
        private val messagesProvider: MessagesProvider = MessagesRepository(),
        private val storageProvider: StorageProvider = StorageRepository(),
        private val chatsProvider: ChatsProvider =  ChatsRepository()
) : ViewModel() ,
        MessagesProvider by messagesProvider,
        StorageProvider by storageProvider,
        ChatsProvider by chatsProvider{


    fun createObserver(id: String): Observable<Chat> = chatsProvider.getChat(id)


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