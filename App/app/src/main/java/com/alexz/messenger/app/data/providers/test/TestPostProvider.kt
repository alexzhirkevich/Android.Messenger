package com.alexz.messenger.app.data.providers.test

import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.entities.interfaces.IPost
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import io.reactivex.Completable
import io.reactivex.Observable

class TestPostProvider : PostsProvider {


    fun newInstance(id : String) =
            Post(
                    id = id,
                    channelId = "test",
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                            "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                            "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
                            "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in " +
                            "culpa qui officia deserunt mollit anim id est laborum",
                    time = System.currentTimeMillis())

    override fun last(chatId: String): Observable<IPost> = Observable.just(
            newInstance("test")
    )

    override fun get(id: String, collectionID: String): Observable<IPost> = Observable.just(
            newInstance("test")
    )

    override fun getAll(collection: IChannel, limit: Int): Observable<List<IPost>>{
        val list = mutableListOf<Post>()
        for (i in 1..limit) {
            list.add(newInstance("test$i"))
        }
        return Observable.just(list)
    }


    override fun create(entity: IPost): Completable = Completable.complete()

    override fun delete(entity: IPost): Completable = Completable.complete()

    override fun remove(id: String, collection: IChannel): Completable = Completable.complete()
}