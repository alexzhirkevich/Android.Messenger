package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.imp.DaggerChatsProviderComponent
import com.alexz.messenger.app.data.providers.imp.UsersProviderImp
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import io.reactivex.Observable

class UserListActivityViewModel : ViewModel(){

    private val usersProvider : UsersProvider by lazy {
        UsersProviderImp()
    }
    private val chatsProvider: ChatsProvider by lazy {
        DaggerChatsProviderComponent.create().getChatsProvider()
    }

    fun getChat(chatId : String) = chatsProvider.get(chatId)

    fun getUser(userID: String): Observable<IUser> = usersProvider.get(id = userID)
}