package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Chat
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ChatsProvider {

    fun getChats(userId : String) : Observable<List<String>>

    fun getUsers(chatId : String) : Observable<List<String>>

    fun getChat(chatId: String) : Observable<Chat>

    fun createChat(chat: Chat): Completable

    fun removeChat(chatId: String) : Completable

    fun joinChat(chatId: String): Single<Chat>
}