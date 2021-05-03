package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import com.alexz.messenger.app.util.FirebaseUtil.LAST_POST
import com.alexz.messenger.app.util.FirebaseUtil.POSTS
import com.alexz.messenger.app.util.FirebaseUtil.channelsCollection
import com.alexz.messenger.app.util.complete
import com.alexz.messenger.app.util.toList
import com.alexz.messenger.app.util.toObjectNonNull
import com.google.firebase.firestore.SetOptions
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

class FirestorePostsProvider : PostsProvider {

    override fun addPost(post: Post): Completable {
        val doc = channelsCollection.document(post.channelId).collection(POSTS).document()
        post.id = doc.id

        val add = Completable.create {
            it.complete(doc.set(post))
        }

        val last = Completable.create {
            it.complete(channelsCollection.document(post.channelId)
                    .set(mapOf(Pair(LAST_POST, post.id)), SetOptions.merge()))
        }

        return Completable.concatArray(add, last)
    }

    override fun lastPost(channelId: String): Observable<Post> = Observable.create {
        channelsCollection.document(channelId).collection(POSTS).limitToLast(1)
                .addSnapshotListener { qs, err ->
                    if (err != null) {
                        it.onError(err)
                    } else if (qs != null) {
                        try {
                            it.onNext(qs.documents.first().toObjectNonNull(Post::class.java))
                        } catch (t: Throwable) {
                            it.tryOnError(t)
                        }
                    }
                }
    }

    fun getPosts(channelId: String, limit: Long): Observable<List<Post>> = Observable.create {
        channelsCollection.document(channelId).collection(POSTS).limitToLast(limit)
                .addSnapshotListener { qs, error ->
                    if (error != null) {
                        it.onError(error)
                    } else {
                        qs?.toList(Post::class.java)?.let { list -> it.onNext(list) }
                    }
                }
    }
}