package com.alexz.messenger.app.data.repo

import com.alexz.messenger.app.data.LocalDatabase
import com.alexz.messenger.app.data.entities.dao.PostsDao
import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.providers.imp.FirestorePostsProvider
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.util.with
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class PostsRepository(
        private val remoteProvider: PostsProvider = FirestorePostsProvider(),
        private val dao: PostsDao = LocalDatabase.INSTANCE.postsDao()) : PostsProvider {

    override fun addPost(post: Post): Completable =
            remoteProvider.addPost(post).andThen {
                it.with(dao.add(post))
            }

    override fun lastPost(channelId: String): Observable<Post> = Observable.concatArray(
            dao.lastPost(channelId).toObservable(),
            remoteProvider.lastPost(channelId))
}