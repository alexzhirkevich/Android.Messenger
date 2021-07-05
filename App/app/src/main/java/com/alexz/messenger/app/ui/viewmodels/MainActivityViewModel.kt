package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.providers.imp.DaggerChatsProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider

class MainActivityViewModel : ViewModel() {

    private val chatsProvider : ChatsProvider by lazy {
        DaggerChatsProviderComponent.create().getChatsProvider()
    }


    fun joinChat(chatId : String) =
            chatsProvider.join(chatId)
}