package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.repo.ChatsRepository

class MainActivityViewModel : ViewModel() {

    val chatsProvider : ChatsProvider by lazy { ChatsRepository(FirestoreChatsProvider()) }


    fun joinChat(chatId : String) =
            chatsProvider.joinChat(chatId)
}