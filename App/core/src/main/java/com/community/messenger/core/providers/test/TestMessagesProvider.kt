package com.community.messenger.core.providers.test

import com.community.messenger.common.entities.imp.Message
import com.community.messenger.common.entities.interfaces.IChat
import com.community.messenger.common.entities.interfaces.IMessage
import com.community.messenger.core.providers.interfaces.MessagesProvider
import io.reactivex.Completable
import io.reactivex.Observable

class TestMessagesProvider : MessagesProvider {

    fun newInstance(id : String) =
            Message(id = id,chatId = "test",text = "Test message text",senderId = "test",
            time = System.currentTimeMillis())


    override fun last(chatId: String): Observable<IMessage> = Observable.just(
            newInstance("test")
    )

    override fun get(id: String, collectionID: String): Observable<IMessage> = Observable.just(
            newInstance(id)
    )
    override fun getAll(collection: IChat, limit: Int): Observable<Collection<IMessage>> =
            Observable.just(
                    listOf(
                            newInstance("test1"),
                            newInstance("test2"),
                            newInstance("test3")
                    )
            )

    override fun create(entity: IMessage): Completable = Completable.complete()

    override fun delete(entity: IMessage): Completable = Completable.complete()

    override fun remove(id: String, collection: IChat): Completable = Completable.complete()
}