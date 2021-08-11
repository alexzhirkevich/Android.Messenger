package com.community.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.components.DaggerChatsProviderComponent
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.ChatsProvider
import com.community.messenger.core.providers.interfaces.UsersProvider
import io.reactivex.Observable

class UserListActivityViewModel : ViewModel(){

    private val usersProvider : UsersProvider by lazy {
        DaggerUsersProviderComponent.create().getProvider()
    }
    private val chatsProvider: ChatsProvider by lazy {
        DaggerChatsProviderComponent.create().getProvider()
    }

    fun getChat(chatId : String) = chatsProvider.get(chatId)

    fun getUser(userID: String): Observable<out IUser> = usersProvider.get(id = userID)
}