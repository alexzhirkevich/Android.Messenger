//package com.alexz.messenger.app.data.repo
//
//import com.alexz.messenger.app.data.LocalDatabase
//import com.alexz.messenger.app.data.entities.IEntityCollection
//import com.alexz.messenger.app.data.entities.dao.PostsDao
//import com.alexz.messenger.app.data.entities.imp.Post
//import com.alexz.messenger.app.data.providers.imp.FirestorePostsProvider
//import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
//import io.reactivex.Completable
//import io.reactivex.Observable
//
//class PostsRepository(
//        private val remoteProvider: PostsProvider = FirestorePostsProvider(),
//        private val dao: PostsDao = LocalDatabase.INSTANCE.postsDao()) : PostsProvider {
//
//    override fun delete(post: Post): Completable =
//            remoteProvider.delete(post).andThen {
//                it.onComplete()
//                dao.delete(post.id)
//            }
//
//    override fun remove(id: String, collection: IEntityCollection?): Completable = remoteProvider.remove(id, collection).andThen {
//        it.onComplete()
//        dao.delete(id)
//    }
//
//    override fun create(entity: Post): Completable =
//            remoteProvider.create(entity).andThen {
//                it.onComplete()
//                dao.add(entity)
//            }
//
//    override fun last(channelId: String): Observable<Post?> = Observable.concatArray(
//            dao.lastPost(channelId).toObservable(),
//            remoteProvider.last(channelId))
//
//    override fun get(id: String, collectionID: String?): Observable<Post> =
//            Observable.concatArray(dao.get(id).toObservable(), remoteProvider.get(id, collectionID))
//
//    override fun getAll(collection: IEntityCollection, limit: Int): Observable<List<Post>> {
//        return if (Post::class.java in collection) {
//            Observable.concatArray(dao.getAll(collection.id, limit).toObservable(), remoteProvider.getAll(collection, limit))
//        } else {
//            Observable.error(IllegalArgumentException("Cannot get posts: invalid collection"))
//        }
//    }
//}