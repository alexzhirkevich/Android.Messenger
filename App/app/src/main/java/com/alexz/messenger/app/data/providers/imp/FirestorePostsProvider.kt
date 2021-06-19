package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.entities.interfaces.IPost
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.util.FirebaseUtil.POSTS
import com.alexz.messenger.app.util.FirebaseUtil.channelsCollection
import com.alexz.messenger.app.util.toCompletable
import com.alexz.messenger.app.util.toObservable
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import io.reactivex.Observable

class FirestorePostsProvider : PostsProvider {

    override fun delete(entity: IPost): Completable =
            channelsCollection.document(entity.channelId).collection(POSTS).document(entity.id).delete()
                    .toCompletable()

    override fun remove(id: String, collection: IChannel): Completable =
            channelsCollection.document(collection.id).collection(POSTS).document(id)
                .delete().toCompletable()

    override fun create(entity: IPost): Completable {

        val collection = channelsCollection.document(entity.channelId).collection(POSTS)

        val document = if (entity.id.isNotEmpty())
            collection.document(entity.id) else collection.document()

        val createTask = document.set(entity.apply { id = document.id }, SetOptions.merge())

//        val setLastPostTask = channelsCollection.document(entity.channelId)
//                .set(mapOf(Pair(LAST_POST, entity.id)))

        return createTask.toCompletable()
    }

    override fun last(channelId: String): Observable<IPost> = Observable.create {
        channelsCollection.document(channelId).collection(POSTS).limitToLast(1)
                .toObservable(Post::class.java)
                .map { list -> list.firstOrNull() }
    }

    override fun get(id: String, collectionID: String): Observable<IPost> =
            channelsCollection.document(collectionID).collection(POSTS).document(id)
                    .toObservable(Post::class.java)
                    .map { it as IPost }

    override fun getAll(collection: IChannel, limit: Int): Observable<List<IPost>> = Observable.create {
        val col = channelsCollection.document(collection.id).collection(POSTS)
        val query = if (limit > -1) col.limitToLast(limit.toLong()) else col

        query.addSnapshotListener { qs, error ->
            if (error != null) {
                it.tryOnError(error)
            } else {
                qs?.toObjects(Post::class.java)?.let { list -> it.onNext(list) }
            }
        }
    }
}
