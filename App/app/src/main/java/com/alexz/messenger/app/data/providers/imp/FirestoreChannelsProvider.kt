package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.ChannelAdmin
import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.UserListProvider
import com.alexz.messenger.app.util.*
import com.alexz.messenger.app.util.FirebaseUtil.ADMINS
import com.alexz.messenger.app.util.FirebaseUtil.CHANNELS
import com.alexz.messenger.app.util.FirebaseUtil.POSTS
import com.alexz.messenger.app.util.FirebaseUtil.REFERENCE
import com.alexz.messenger.app.util.FirebaseUtil.USERS
import com.alexz.messenger.app.util.FirebaseUtil.channelsCollection
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class FirestoreChannelsProvider(
        private val userListProvider: UserListProvider = FirestoreUserListProvider()) : ChannelsProvider {

    override fun getChannel(channelId: String): Observable<Channel> =
        firestoreObservable(channelsCollection.document(channelId), Channel::class.java)

    override fun getChannels(userId: String): Observable<List<String>> = Observable.create {
        usersCollection.document(userId).collection(CHANNELS).get()
                .addOnSuccessListener { qs ->
                    it.onNext(qs.documents.mapNotNull { doc -> doc.id })
                }.addOnFailureListener { ex -> it.onError(ex) }
    }

    override fun getUsers(channelId: String): Observable<List<String>> = Observable.create {
        channelsCollection.document(channelId).collection(USERS).get()
                .addOnSuccessListener { qs ->
                    it.onNext(qs.documents.mapNotNull { doc -> doc.id })
                }.addOnFailureListener { ex -> it.onError(ex) }
    }

    override fun createChannel(channel: Channel): Completable {

        val doc = channelsCollection.document()
        val uId = User().id

        channel.id = doc.id

        val creationCompletable = Completable.create { it.complete(doc.set(channel)) }

        val userCompletable = taskCompletable(doc.collection(USERS).document(uId)
                    .set(mapOf(Pair(REFERENCE,usersCollection.document(uId)))))

        val adminCompletable = taskCompletable(doc.collection(ADMINS).document(uId).set(
                    ChannelAdmin(canPost = true, canDelete = true, canBan = true, canEdit = true)))

        val profileCompletable = taskCompletable(usersCollection.document(uId).collection(CHANNELS).document(channel.id)
                    .set(mapOf(Pair(REFERENCE, channelsCollection.document(channel.id)))))

        return Completable.concatArray(creationCompletable, userCompletable, adminCompletable, profileCompletable)
    }

    override fun removeChannel(channel: Channel): Completable {

        val completable = Completable.create {
            if (channel.creatorId == User().id) {
                it.complete(channelsCollection.document(channel.id).delete())
            } else it.onComplete()
        }

        return Completable.concatArray(completable, userListProvider.onChannelLeft(channel.id))
    }

    override fun joinChannel(channelId: String): Single<Channel> {

        val uid = User().id
        val doc = channelsCollection.document(channelId).collection(USERS).document(uid)

        val check1 = Completable.create {
            getUsers(uid).singleOrError().doOnSuccess { list ->
                list.find { id -> id == channelId }?.let { _ -> it.onComplete() }
                        ?: it.tryOnError(InterruptingThrowable)
            }.subscribe()
        }

        var channel: Channel? = null

        val check2 = Completable.create {
            doc.get().addOnSuccessListener { ds ->
                if (ds.exists()) {
                    channel = ds.toObject(Channel::class.java)
                    it.onComplete()
                } else {
                    it.tryOnError(InterruptingThrowable)
                }
            }.addOnFailureListener { _ ->
                it.tryOnError(InterruptingThrowable)
            }
        }

        val joinCompletable = Completable.concatArray(
                taskCompletable(doc.set(mapOf(Pair(REFERENCE, usersCollection.document(uid))))),
                userListProvider.onChannelJoined(channelId))

        return Single.create<Channel> {
            Completable.concatArray(check1, check2)
                    .doOnError { ex ->
                        if (ex is InterruptingThrowable) {
                            joinCompletable.subscribe(
                                    { channel?.let { c -> it.onSuccess(c) } ?: it.tryOnError(NullPointerException())},
                                    { t -> it.tryOnError(t) })
                        } else {
                            it.tryOnError(ex)
                        }
                    }.subscribe()
        }
    }

    override fun addPost(post: Post): Completable = Completable.create {
        val doc = channelsCollection.document(post.channelId)
                .collection(POSTS)
                .document()
        post.id = doc.id
        doc.set(post).addOnSuccessListener {_ ->
            it.onComplete()
        }.addOnFailureListener { t -> it.tryOnError(t) }
    }

    override fun lastPost(channelId: String): Observable<Post> = Observable.create {
        channelsCollection.document(channelId).collection(POSTS).limitToLast(1).get()
                .addOnSuccessListener { qs ->
                    try {
                        it.onNext(qs.documents[0].toObjectNonNull(Post::class.java))
                    } catch (t: Throwable) {
                        it.tryOnError(t)
                    }
                }
    }

    override fun findChannels(namePart: String): Single<List<Channel>> = Single.create {
        it.parseListNonNull(
                channelsCollection.whereGreaterThanOrEqualTo(FirebaseUtil.SEARCH_NAME, namePart).get(),
                Channel::class.java)
    }
}