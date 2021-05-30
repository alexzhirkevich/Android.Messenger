package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.ChannelAdmin
import com.alexz.messenger.app.data.entities.imp.User
import io.reactivex.Observable
import io.reactivex.Single

interface ChannelsProvider : EntityProvider<Channel>  {

    fun getUsers(channelId: String,limit:Int = 30) : Observable<List<User>>

    fun find(namePart: String): Single<List<Channel>>

    fun join(channelId: String): Single<Channel>

    fun getAdmins(channelId: String) : Observable<List<ChannelAdmin>>
}