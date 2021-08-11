package com.community.messenger.core.providers.imp

import com.community.messenger.common.entities.imp.Post
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.entities.interfaces.IPost
import com.community.messenger.common.util.toCompletable
import com.community.messenger.common.util.toObservable
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.COLLECTION_POSTS
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_TIME
import com.community.messenger.core.providers.interfaces.PostsProvider
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsProviderImp @Inject constructor(
        private val firebaseProvider: FirebaseProvider
): PostsProvider {

    override fun delete(entity: IPost): Completable =
            firebaseProvider.channelsCollection.document(entity.channelId).collection(COLLECTION_POSTS).document(entity.id).delete()
                    .toCompletable()

    override fun remove(id: String, collection: IChannel): Completable =
            firebaseProvider.channelsCollection.document(collection.id).collection(COLLECTION_POSTS).document(id)
                .delete().toCompletable()

    override fun create(entity: IPost): Completable {

        val doc = firebaseProvider.channelsCollection
            .document(entity.channelId).collection(COLLECTION_POSTS)
            .document()

        entity.id = doc.id

        return  doc.set(entity).toCompletable()

    }

//    override fun last(channelId: String): Observable<out IPost> = Observable.create {
//        firebaseProvider.channelsCollection.document(channelId).collection(COLLECTION_POSTS)
//            .orderBy(FIELD_TIME).limitToLast(1)
//                .toObservable(Post::class.java)
//                .map { list -> list.first() }
//    }

    override fun get(id: String, collectionID: String): Observable<out IPost> =
            firebaseProvider.channelsCollection.document(collectionID).collection(COLLECTION_POSTS).document(id)
                    .toObservable(Post::class.java)

    override fun getAll(collection: IChannel, limit: Int): Observable<Collection<IPost>> {
        val col = firebaseProvider.channelsCollection.document(collection.id).collection(COLLECTION_POSTS)
            .orderBy(FIELD_TIME)
        val query = if (limit > -1) col.limitToLast(limit.toLong()) else col

        return query.toObservable(Post::class.java).map { it }
    }

}

