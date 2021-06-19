package com.alexz.messenger.app.data.providers.test

import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.entities.interfaces.IUserContainer
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class TestUsersProvider :UsersProvider{
    override fun getNotificationToken(userId: String): Single<String> = Single.just("test")


    override fun get(id: String): Observable<IUser> = Observable.just(
            User(id = "test",imageUri = "",name = "Test",isOnline = true)
    )


    override fun getAll(collection: IUserContainer, limit: Int): Observable<List<IUser>>
            = Observable.just(
            listOf(
                    User(id = "test1",imageUri = "",name = "Test",isOnline = true),
                    User(id = "test2",imageUri = "",name = "Test",isOnline = true),
                    User(id = "test3",imageUri = "",name = "Test",isOnline = true)
            )
    )

    override fun create(entity:IUser): Completable = Completable.complete()

    override fun delete(entity: IUser): Completable = Completable.complete()

}