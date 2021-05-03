package com.alexz.messenger.app.data.repo

import com.alexz.messenger.app.data.LocalDatabase
import com.alexz.messenger.app.data.entities.dao.ChannelsDao
import com.alexz.messenger.app.data.entities.dao.PostsDao
import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.providers.imp.FirestoreChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.util.FirebaseUtil
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class ChannelsRepository(
        private val provider: ChannelsProvider = FirestoreChannelsProvider(),
        private val channelsDao: ChannelsDao = LocalDatabase.INSTANCE.channelDao(),
        private val postsDao: PostsDao = LocalDatabase.INSTANCE.postsDao()
) : ChannelsProvider by provider  {

    override fun getChannel(channelId: String): Observable<Channel> =
            channelsDao.get(channelId).toObservable().concatWith(provider.getChannel(channelId))

    override fun getChannels(userId: String): Observable<List<String>> =
            Observable.concatArray(
                    channelsDao.getAll()
                            .toObservable()
                            .flatMapIterable { list -> list }
                            .map { c -> c.id }
                            .toList()
                            .toObservable(),
                    provider.getChannels(userId))

    override fun removeChannel(channel: Channel): Completable =
            provider.removeChannel(channel).andThen { channelsDao.delete(channel.id) }


    override fun joinChannel(channelId: String): Single<Channel> = Single.create {
        provider.joinChannel(channelId)
                .doOnSuccess { c ->
                    channelsDao.add(c)
                    it.onSuccess(c)
                }.doOnError { t ->
                    it.onError(t)
                }.subscribe()
    }

    override fun addPost(post: Post): Completable =
            provider.addPost(post).andThen { postsDao.add(post) }

    override fun lastPost(channelId: String): Observable<Post> = Observable.concatArray(
            postsDao.lastPost(channelId).toObservable(),provider.lastPost(channelId)
    )

    companion object {

        @JvmStatic
        fun createInviteLink(id: String): String {
            return buildString {
                append(FirebaseUtil.URL_BASE)
                append(FirebaseUtil.LINK_CHANNEL)
                append('/')
                append(id)
            }
        }
    }
}