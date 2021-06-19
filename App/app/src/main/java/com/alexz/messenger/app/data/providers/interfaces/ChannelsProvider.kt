package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.ChannelAdmin
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.base.DependentRangeEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRemovable
import com.alexz.messenger.app.data.providers.base.SingleEntityProvider
import io.reactivex.Observable
import io.reactivex.Single

interface ChannelsProvider :
        SingleEntityProvider<IChannel>,
        DependentRangeEntityProvider<IChannel,IUser>,
        DependentRemovable<IUser>{

    fun getUsers(channelId: String,limit:Int = 30) : Observable<List<IUser>>

    fun find(namePart: String): Single<List<IChannel>>

    fun join(channelId: String): Single<IChannel>

    fun getAdmins(channelId: String) : Observable<List<ChannelAdmin>>
}