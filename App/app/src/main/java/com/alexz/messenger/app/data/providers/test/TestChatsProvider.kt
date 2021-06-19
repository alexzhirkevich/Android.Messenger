package com.alexz.messenger.app.data.providers.test

import com.alexz.messenger.app.data.entities.imp.Chat
import com.alexz.messenger.app.data.entities.interfaces.IMessageable
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

class TestChatsProvider : ChatsProvider {

    fun newInstance(id :String) =
            Chat(id = id,name = "Test",lastMessageId = "test",lastMessageTime = 0)

    override fun getUsers(chatId: String, limit: Int): Observable<List<IUser>> =
            TestUsersProvider().getAll(Chat(id = chatId))

    override fun join(chatId: String): Maybe<IMessageable> = Maybe.just(
            newInstance(chatId)
    )

    override fun get(id: String): Observable<IMessageable> = Observable.just(
            newInstance(id)
    )

    @ExperimentalStdlibApi
    override fun getAll(collection: IUser, limit: Int): Observable<List<IMessageable>> {
        val list = buildList<IMessageable> {
            repeat(limit) {
                add(newInstance("test$it"))
            }
        }
        return Observable.just(list)
    }

    override fun create(entity: IMessageable): Completable = Completable.complete()

    override fun delete(entity: IMessageable): Completable = Completable.complete()

    override fun remove(id: String, collection: IUser): Completable = Completable.complete()
}