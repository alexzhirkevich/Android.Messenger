package com.alexz.messenger.app.data.providers.test

import com.alexz.messenger.app.data.entities.imp.Group
import com.alexz.messenger.app.data.entities.interfaces.IChat
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.interfaces.ChatsProvider
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

class TestChatsProvider : ChatsProvider {

    fun newInstance(id :String) =
            Group(id = id,name = "Test Test",lastMessageId = "test",lastMessageTime = 0)

    override fun getUsers(chatId: String, limit: Int): Observable<List<IUser>> = Observable.create {
    }

    override fun join(chatId: String): Maybe<IChat> = Maybe.just(
            newInstance(chatId)
    )

    override fun get(id: String): Observable<IChat> = Observable.just(
            newInstance(id)
    )

    @ExperimentalStdlibApi
    override fun getAll(collection: IUser, limit: Int): Observable<List<IChat>> {
        val list = buildList<IChat> {
            repeat(limit) {
                add(newInstance("test$it"))
            }
        }
        return Observable.just(list)
    }

    override fun create(entity: IChat): Completable = Completable.complete()

    override fun delete(entity: IChat): Completable = Completable.complete()

    override fun remove(id: String, collection: IUser): Completable = Completable.complete()
}