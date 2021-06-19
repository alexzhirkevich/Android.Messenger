package com.alexz.messenger.app.data.providers.test

import com.alexz.messenger.app.data.entities.imp.Post
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.entities.interfaces.IPost
import com.alexz.messenger.app.data.providers.interfaces.PostsProvider
import io.reactivex.Completable
import io.reactivex.Observable

class TestPostProvider : PostsProvider {


    fun newInstance(id : String) =
            Post(id = id,channelId = "test",text = "w w w w w w w w w w w w w w w w w w  w w w w w w w w w w w ww ",time = System.currentTimeMillis())

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