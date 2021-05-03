package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.imp.FirestoreChatsProvider
import com.alexz.messenger.app.data.providers.imp.FirestoreUserListProvider
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.alexz.messenger.app.data.repo.ChatsRepository
import com.alexz.messenger.app.data.repo.UserListRepository
import io.reactivex.rxjava3.core.Observable

class UserListActivityViewModel : ViewModel(), UserListProvider{

    private val usersProvider : UserListProvider by lazy {
        UserListRepository(FirestoreUserListProvider())
    }
    private val chatsProvider: ChatsProvider by lazy {
        ChatsRepository(FirestoreChatsProvider())
    }

    fun getChat(chatId : String) = chatsProvider.getChat(chatId)

    fun getUsers(chatId : String) : Observable<List<User>> =
        chatsProvider.getUsers(chatId)
                .flatMapIterable<String> { ids -> ids }
                .flatMap { id -> usersProvider.getUser(id) }
                .toList()
                .toObservable()

//        chatsProvider.getUsers(chatId).subscribe(
//                {list ->
//                    Observable.fromIterable(list.map { el -> usersProvider.getUser(el) })
//                            .flatMapIterable { l -> l }
//                            .toList()
//                            .toObservable()
//                },
//                { t -> it.tryOnError(t)},
//                { it .onComplete()}
//        )
//    }


    override fun getUser(userID: String): Observable<User> =
            usersProvider.getUser(userID)
}