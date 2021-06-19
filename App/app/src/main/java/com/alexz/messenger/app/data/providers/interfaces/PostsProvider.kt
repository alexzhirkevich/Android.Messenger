package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.entities.interfaces.IPost
import com.alexz.messenger.app.data.providers.base.DependentEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRangeEntityProvider
import com.alexz.messenger.app.data.providers.base.DependentRemovable
import io.reactivex.Observable

interface PostsProvider :
        DependentEntityProvider<IPost, IChannel>,
        DependentRangeEntityProvider<IPost,IChannel>,
        DependentRemovable<IChannel>{
    fun last(channelId: String) : Observable<IPost>
}