package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.entities.interfaces.IUserContainer
import com.alexz.messenger.app.data.providers.base.DependentRangeEntityProvider
import com.alexz.messenger.app.data.providers.base.SingleEntityProvider
import io.reactivex.Completable
import io.reactivex.Single

interface UsersProvider
    : SingleEntityProvider<IUser>,
        DependentRangeEntityProvider<IUser,IUserContainer> {

    fun getNotificationToken(userId : String) : Single<String>

    fun onChannelJoin(channelId : String) : Completable = Completable.complete()

    fun leaveChannel(userId: String,channelId : String) : Completable = Completable.complete()

    fun joinChat(chatId : String) : Completable = Completable.complete()

    fun leaveChat(userId: String,chatId : String) : Completable = Completable.complete()
}