package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.imp.Post
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface PostsProvider {
    fun addPost(post: Post): Completable

    fun lastPost(channelId: String) : Observable<Post>
}