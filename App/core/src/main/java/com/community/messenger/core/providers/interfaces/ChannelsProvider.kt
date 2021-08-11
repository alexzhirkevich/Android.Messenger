package com.community.messenger.core.providers.interfaces

import com.community.messenger.common.entities.imp.ChannelAdmin
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.core.providers.base.DependentRangeEntityProvider
import com.community.messenger.core.providers.base.DependentRemovable
import com.community.messenger.core.providers.base.LinkProvider
import com.community.messenger.core.providers.base.SingleEntityProvider
import io.reactivex.Observable
import io.reactivex.Single

interface ChannelsProvider :
        SingleEntityProvider<IChannel>,
        DependentRangeEntityProvider<IChannel,IUser>,
        DependentRemovable<IUser>,
        LinkProvider {

    fun getUsers(channelId: String,limit:Int = 30) : Observable<out Collection<IUser>>

    fun find(namePart: String,limit: Int = 30): Observable<Collection<IChannel>>

    fun join(channelId: String): Single<out IChannel>

    fun getAdmins(channelId: String) : Observable<out Collection<ChannelAdmin>>

    fun getSubscribersCount(channelId: String) : Observable<Long>

    fun getByTag(tag : String) : Observable<IChannel>

    fun findByTag(tag: String): Observable<IChannel>
}