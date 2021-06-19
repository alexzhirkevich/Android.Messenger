package com.alexz.messenger.app.data.providers.test

import com.alexz.messenger.app.data.entities.imp.Channel
import com.alexz.messenger.app.data.entities.imp.ChannelAdmin
import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IChannel
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.interfaces.ChannelsProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

class TestChannelsProvider : ChannelsProvider {
    override fun getUsers(channelId: String, limit: Int): Observable<List<IUser>> =
            TestUsersProvider().getAll(Channel(channelId), limit)

    override fun find(namePart: String): Single<List<IChannel>> =
            getAll(User()).map { list ->
                list.filter { c ->
                    c.name.toLowerCase(Locale.getDefault())
                            .contains(namePart.toLowerCase(Locale.getDefault()))
                }
            }.singleElement().toSingle()

    override fun join(channelId: String): Single<IChannel> = get(channelId).singleOrError()

    override fun getAdmins(channelId: String): Observable<List<ChannelAdmin>> =
            getUsers(channelId).map { list ->
                list.map { user -> ChannelAdmin(id = user.id) }
            }

    override fun get(id: String): Observable<IChannel> =
            Observable.just(Channel(id = id))


    override fun getAll(collection: IUser, limit: Int): Observable<List<IChannel>> {
        val list = mutableListOf<Channel>()
        for (i in 1..limit){
            list.add(Channel(id = "test$i", name = "Test", lastPostId = "test", lastPostTime = System.currentTimeMillis()))
        }
        return Observable.just(list)
    }

    override fun create(entity: IChannel): Completable = Completable.complete()

    override fun delete(entity: IChannel): Completable = Completable.complete()

    override fun remove(id: String, collection: IUser): Completable = Completable.complete()
}