package com.community.messenger.app.ui.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import com.community.messenger.app.data.ChatApplication.Companion.AppContext
import com.community.messenger.common.entities.imp.Message
import com.community.messenger.common.entities.imp.User
import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.core.providers.components.DaggerMessagesProviderComponent
import com.community.messenger.core.providers.components.DaggerStorageProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.ChatsProvider
import com.community.messenger.core.providers.interfaces.MessagesProvider
import com.community.messenger.core.providers.interfaces.StorageProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.community.messenger.core.providers.test.TestChatsProvider
import kotlinx.coroutines.rx2.asFlow

class ChatsViewModel : DataViewModel<Collection<IChat>>() {

    private val messagesProvider: MessagesProvider by lazy {
        DaggerMessagesProviderComponent.create().getProvider()
    }
    private val storageProvider: StorageProvider by lazy {
        DaggerStorageProviderComponent.builder()
            .setContext(AppContext)
            .build()
            .getProvider()
    }


    private val chatsProvider: ChatsProvider by lazy { TestChatsProvider() }

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }

    init {
        collect(chatsProvider.getAll(User(id =usersProvider.currentUserId),limit = 30).asFlow())
    }

    fun getChat(chatId:String) = chatsProvider.get(chatId)

    fun sendMessage(message: Message) = messagesProvider.create(message)

    fun uploadImage(uri: Uri) = storageProvider.uploadImage(uri)
    fun uploadVoice (uri: Uri) = storageProvider.uploadVoice(uri)

    fun deleteMessage(message: Message) = messagesProvider.delete(message)

    fun copyInviteLink(chatId: String) {
        val cd = ClipData.newPlainText("fm-chat-invite","TODO: ВЕРНУТЬ ЛИНК ПРОВАЙДЕР" /*FirebaseProviderImp.createChatInviteLink(chatId)*/)
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