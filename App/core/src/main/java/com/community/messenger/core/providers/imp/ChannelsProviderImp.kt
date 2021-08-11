package com.community.messenger.core.providers.imp

import com.community.messenger.common.entities.imp.Channel
import com.community.messenger.common.entities.imp.ChannelAdmin
import com.community.messenger.common.entities.imp.User
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.util.InterruptingThrowable
import com.community.messenger.common.util.merge
import com.community.messenger.common.util.toCompletable
import com.community.messenger.common.util.toObservable
import com.community.messenger.core.providers.interfaces.ChannelsProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_ADMINS
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_CHANNELS
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_USERS
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_LAST_POST_TIME
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_REFERENCE
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_SEARCH_NAME
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_SUBCRIBERS
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_TAG
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_TAG_SEARCH
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.LINK_CHANNEL
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.PRIVATE
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.PUBLIC
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.URL_BASE
import com.community.messenger.core.providers.interfaces.UsersProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelsProviderImp @Inject constructor(
        private val userListProvider: UsersProvider,
        private val firebaseProvider: FirebaseProvider
) : ChannelsProvider  {

    override fun getAdmins(channelId: String): Observable<out Collection<ChannelAdmin>> =
            firebaseProvider.channelsCollection.document(channelId).collection(COLLECTION_ADMINS).toObservable(ChannelAdmin::class.java)

    override fun getSubscribersCount(channelId: String): Observable<Long> =
        firebaseProvider.channelsCollection.document(channelId)
            .collection(PRIVATE).document(PUBLIC)
            .toObservable{it[FIELD_SUBCRIBERS].toString().toLong()}

    override fun getByTag(tag: String): Observable<IChannel> =
        firebaseProvider.channelsCollection.whereEqualTo(FIELD_TAG,tag).limit(1)
                .toObservable(Channel::class.java).map { it.first() }


    override fun findByTag(tag: String): Observable<IChannel> =
            firebaseProvider.channelsCollection.whereEqualTo(FIELD_TAG,tag)
                    .toObservable(Channel::class.java).map { it.first() }


    override fun get(id: String): Observable<IChannel> =
            firebaseProvider.channelsCollection.document(id).toObservable(Channel::class.java)
                .map { it }


    override fun getAll(collection: IUser, limit: Int): Observable<Collection<IChannel>> {

        val col = firebaseProvider.usersCollection.document(collection.id).collection(COLLECTION_CHANNELS)
        val task = if (limit > -1) col.orderBy(FIELD_LAST_POST_TIME).limitToLast(limit.toLong()) else col

//        return Observable.create {
//            col.toObservable { doc -> get(doc.id) }.subscribe { list ->
//                list.merge().subscribe { v-> it.onNext(v)}
//            }
//        }
        return task.toObservable { doc -> get(doc.id) }.flatMap { it.merge() }
    }

    override fun getUsers(channelId: String, limit: Int): Observable<out Collection<IUser>> = Observable.create {
       // userListProvider.getAll(Channel(id = channelId),limit)
    }

    override fun create(entity: IChannel): Completable {

        val doc = firebaseProvider.channelsCollection.document()
        val uId = userListProvider.currentUserId

        entity.id = doc.id
        entity.creatorId=uId
        entity.creationTime=System.currentTimeMillis()

        val map = IChannel::class.java.fields.mapNotNull {
            if (it.name != "subscribers")
                it.name to it.get(entity)
            else null
        }.toMap()

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
//        return Completable.mergeArray(creationCompletable, userCompletable, adminCompletable, profileCompletable)
    }

    override fun delete(entity: IChannel): Completable {

        if (entity.creatorId == userListProvider.currentUserId){
            return firebaseProvider.channelsCollection.document(entity.id).delete().toCompletable()
        }
        return remove(entity.id, User(id = userListProvider.currentUserId))
    }

    override fun remove(id: String,collection: IUser): Completable = Completable.complete()

    override fun join(channelId: String): Single<out IChannel> {

        val uid = userListProvider.currentUserId
        val doc = firebaseProvider.channelsCollection.document(channelId).collection(COLLECTION_USERS).document(uid)

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

        val joinCompletable = doc.set(mapOf(Pair(FIELD_REFERENCE, firebaseProvider.usersCollection.document(uid))))
                .toCompletable()

        return Single.create<IChannel> {
            Completable.mergeArray(check1, check2)
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

    override fun find(namePart: String,limit: Int): Observable<Collection<IChannel>> =
            listOf(
                firebaseProvider.channelsCollection
                    .whereGreaterThanOrEqualTo(FIELD_SEARCH_NAME, namePart).apply {
                if (limit>2)
                    limit(limit/2.toLong())
                else if (limit>1)
                    limit(limit.toLong())
            }.toObservable(Channel::class.java).map { it },
                firebaseProvider.channelsCollection
                    .whereGreaterThanOrEqualTo(FIELD_TAG_SEARCH, namePart).apply {
                        if (limit>2)
                            limit(limit/2.toLong())
                        else if (limit>1)
                            limit(limit.toLong())
                    }.toObservable(Channel::class.java).map { it }
            ).merge().map { it.flatten() }

    override fun createInviteLink(id: String): String {
        return buildString {
            append(URL_BASE)
            append(LINK_CHANNEL)
            append('/')
            append(id)
        }
    }
}

