package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider

class MainActivityViewModel : ViewModel() {

    val chatsProvider : ChatsProvider by lazy { FirestoreChatsProvider() }


    fun joinChat(chatId : String) =
            chatsProvider.join(chatId)
}