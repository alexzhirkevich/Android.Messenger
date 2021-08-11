//package com.community.messenger.app.data.repo
//
//import com.community.messenger.app.data.LocalDatabase
//import com.community.messenger.common.entities.IEntityCollection
//import com.community.messenger.common.entities.dao.PostsDao
//import com.community.messenger.common.entities.imp.Post
//import com.community.messenger.core.providers.imp.FirestorePostsProvider
//import com.community.messenger.core.providers.interfaces.PostsProvider
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
//    override fun last(channelId: String): Observable<Post?> = Observable.mergeArray(
//            dao.lastPost(channelId).toObservable(),
//            remoteProvider.last(channelId))
//
//    override fun get(id: String, collectionID: String?): Observable<Post> =
//            Observable.mergeArray(dao.get(id).toObservable(), remoteProvider.get(id, collectionID))
//
//    override fun getAll(collection: IEntityCollection, limit: Int): Observable<List<Post>> {
//        return if (Post::class.java in collection) {
//            Observable.mergeArray(dao.getAll(collection.id, limit).toObservable(), remoteProvider.getAll(collection, limit))
//        } else {
//            Observable.error(IllegalArgumentException("Cannot get posts: invalid collection"))
//        }
//    }
//}