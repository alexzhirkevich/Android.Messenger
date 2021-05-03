package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.User
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface UserListProvider {

    fun getUser(userID: String) : Observable<User>

    fun onChannelJoined(channelId : String) : Completable = Completable.complete()

    fun onChannelLeft(channelId : String) : Completable = Completable.complete()

    fun onChatJoined(chatId : String) : Completable = Completable.complete()

    fun onChatLeft(chatId : String) : Completable = Completable.complete()
}