package com.community.messenger.core.providers.interfaces

import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.entities.interfaces.IPost
import com.community.messenger.core.providers.base.DependentEntityProvider
import com.community.messenger.core.providers.base.DependentRangeEntityProvider
import com.community.messenger.core.providers.base.DependentRemovable

interface PostsProvider :
        DependentEntityProvider<IPost, IChannel>,
        DependentRangeEntityProvider<IPost,IChannel>,
        DependentRemovable<IChannel>{
//    fun last(channelId: String) : Observable<out IPost>
}