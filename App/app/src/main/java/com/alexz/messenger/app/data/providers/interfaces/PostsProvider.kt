package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Post
import io.reactivex.Observable

interface PostsProvider : EntityProvider<Post> {

    fun last(channelId: String) : Observable<Post>
}