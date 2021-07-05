package com.alexz.messenger.app.ui.viewmodels

import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import androidx.core.content.ContextCompat.getSystemService
import com.alexz.messenger.app.data.ChatApplication.Companion.AppContext
import com.alexz.messenger.app.data.entities.imp.Message
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.data.providers.imp.DaggerMessagesProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerStorageProviderComponent
import com.alexz.messenger.app.data.providers.imp.DaggerUsersProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.MessagesProvider
import com.alexz.messenger.app.data.providers.interfaces.StorageProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.data.providers.test.TestChatsProvider
import com.alexz.messenger.app.util.FirebaseUtil
import io.reactivex.schedulers.Schedulers

class ChatsViewModel : DataViewModel<List<IChat>>(), Updatable {

    private val messagesProvider: MessagesProvider by lazy {
        DaggerMessagesProviderComponent.create().getMessagesProvider()
    }
    private val storageProvider: StorageProvider by lazy {
        DaggerStorageProviderComponent.create().getStorageProvider()
    }

    private val chatsProvider: ChatsProvider by lazy { TestChatsProvider() }

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getUsersProvider()
    }

    override fun update() {
        observe(chatsProvider.getAll(User(id =usersProvider.currentUserId),limit = 30)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .sorted())
    }

    fun getChat(chatId:String) = chatsProvider.get(chatId)

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