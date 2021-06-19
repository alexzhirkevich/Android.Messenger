package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.ChannelAdmin
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.data.repo.LinkProvider
import com.alexz.messenger.app.util.*
import com.alexz.messenger.app.util.FirebaseUtil.ADMINS
import com.alexz.messenger.app.util.FirebaseUtil.CHANNELS
import com.alexz.messenger.app.util.FirebaseUtil.REFERENCE
import com.alexz.messenger.app.util.FirebaseUtil.TIME
import com.alexz.messenger.app.util.FirebaseUtil.USERS
import com.alexz.messenger.app.util.FirebaseUtil.channelsCollection
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class FirestoreChannelsProvider(
        private val userListProvider: UsersProvider = FirestoreUsersProvider()
) : ChannelsProvider, LinkProvider {

    override fun getAdmins(channelId: String): Observable<List<ChannelAdmin>> =
        channelsCollection.document(channelId).collection(ADMINS).toObservable(ChannelAdmin::class.java)

    override fun get(id: String): Observable<IChannel> =
            channelsCollection.document(id).toObservable(Channel::class.java).map { it as IChannel }

    override fun getAll(collection: IUser, limit: Int): Observable<List<IChannel>> = Observable.create {

        val col = usersCollection.document(collection.id).collection(CHANNELS)
        val task = if (limit > -1) col.orderBy(TIME).limitToLast(limit.toLong()).get() else col.get()

        task.addOnSuccessListener { qs ->
            it.onNext(
                    qs.documents.mapNotNull { doc -> doc.toObject(Channel::class.java) }
            )
        }.addOnFailureListener { ex -> it.onError(ex) }
    }

    override fun getUsers(channelId: String, limit: Int): Observable<List<IUser>> = Observable.create {
        userListProvider.getAll(Channel(id = channelId),limit)
    }

    override fun create(entity: IChannel): Completable {

        val doc = channelsCollection.document()
        val uId = User().id

        entity.id = doc.id

        return doc.set(entity).toCompletable()

//        val userCompletable = (doc.collection(USERS).document(uId)
//                    .set(mapOf(Pair(REFERENCE,usersCollection.document(uId))))).toCompletable()
//
//        val adminCompletable = doc.collection(ADMINS).document(uId).set(
//                    ChannelAdmin(canPost = true, canDelete = true, canBan = true, canEdit = true)).toCompletable()
//
//        val profileCompletable = usersCollection.document(uId).collection(CHANNELS).document(entity.id)
//                    .set(mapOf(Pair(REFERENCE, channelsCollection.document(entity.id)))).toCompletable()
//
//        return Completable.concatArray(creationCompletable, userCompletable, adminCompletable, profileCompletable)
    }

    override fun delete(entity: IChannel): Completable {

        if (entity.creatorId == User().id){
            return channelsCollection.document(entity.id).delete().toCompletable()
        }
        return remove(entity.id,User())
    }

    override fun remove(id: String,collection: IUser): Completable = Completable.complete()

    override fun join(channelId: String): Single<IChannel> {

        val uid = User().id
        val doc = channelsCollection.document(channelId).collection(USERS).document(uid)

        val check1 = Completable.create {
            getUsers(uid).singleOrError()
                    .subscribe(
                            { list ->
                                list.find { user -> user.id == channelId }?.let { _ -> it.onComplete() }
                                        ?: it.tryOnError(InterruptingThrowable)
                            },
                            {})
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
                doc.set(mapOf(Pair(REFERENCE, usersCollection.document(uid)))).toCompletable(),
                userListProvider.onChannelJoin(channelId))

        return Single.create<IChannel> {
            Completable.concatArray(check1, check2)
                    .subscribe(
                            {
                                channel?.let { it1 -> it.onSuccess(it1) }
                            },
                            { ex ->
                                if (ex is InterruptingThrowable) {
                                    joinCompletable.subscribe(
                                            { channel?.let { c -> it.onSuccess(c) } ?: it.tryOnError(NullPointerException())},
                                            { t -> it.tryOnError(t) })
                                } else {
                                    it.tryOnError(ex)
                                }
                            }
                    )
        }
    }

    override fun find(namePart: String): Single<List<IChannel>> =
            channelsCollection.whereGreaterThanOrEqualTo(FirebaseUtil.SEARCH_NAME, namePart).get()
                    .toSingle().map { snap -> snap.toObjects(Channel::class.java) }

    override fun createInviteLink(id: String): String {
        return buildString {
            append(FirebaseUtil.URL_BASE)
            append(FirebaseUtil.LINK_CHANNEL)
            append('/')
            append(id)
        }
    }
}