package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreUsersProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import io.reactivex.Observable

class UserListActivityViewModel : ViewModel(){

    private val usersProvider : UsersProvider by lazy {
        FirestoreUsersProvider()
    }
    private val chatsProvider: ChatsProvider by lazy {
        FirestoreChatsProvider()
    }

    fun getChat(chatId : String) = chatsProvider.get(chatId)

    fun getUser(userID: String): Observable<IUser> = usersProvider.get(id = userID)
}