package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.User
import io.reactivex.Completable
import io.reactivex.Single

interface UserListProvider : EntityProvider<User> {

    fun getNotificationToken(userId : String) : Single<String>

    fun onChannelJoin(channelId : String) : Completable = Completable.complete()

    fun onChannelLeft(channelId : String) : Completable = Completable.complete()

    fun onChatJoin(chatId : String) : Completable = Completable.complete()

    fun onChatLeft(chatId : String) : Completable = Completable.complete()
}