package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Channel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ChannelsProvider : PostsProvider {

    fun getChannel(channelId: String) : Observable<Channel>

    fun getChannels(userId: String) : Observable<List<String>>

    fun getUsers(channelId: String) : Observable<List<String>>

    fun createChannel(channel: Channel): Completable

    fun removeChannel(channel: Channel) : Completable

    fun findChannels(namePart: String): Single<List<Channel>>

    fun joinChannel(channelId: String): Single<Channel>
}