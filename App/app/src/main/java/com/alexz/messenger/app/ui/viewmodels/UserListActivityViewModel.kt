package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreUserListProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import io.reactivex.Observable

class UserListActivityViewModel : ViewModel(){

    private val usersProvider : UserListProvider by lazy {
        FirestoreUserListProvider()
    }
    private val chatsProvider: ChatsProvider by lazy {
        FirestoreChatsProvider()
    }

    fun getChat(chatId : String) = chatsProvider.get(chatId)


    fun getUser(userID: String): Observable<User> =
            usersProvider.get(id = userID)
}