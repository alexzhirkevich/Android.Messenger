package com.alexz.messenger.app.data.providers.imp

import com.alexz.messenger.app.data.entities.imp.User
import com.alexz.messenger.app.data.entities.interfaces.IUser
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.util.FirebaseUtil.ID
import com.alexz.messenger.app.util.FirebaseUtil.NOTIFY_TOKEN
import com.alexz.messenger.app.util.FirebaseUtil.PHONE
import com.alexz.messenger.app.util.FirebaseUtil.USERNAME
import com.alexz.messenger.app.util.FirebaseUtil.usersCollection
import com.alexz.messenger.app.util.merge
import com.alexz.messenger.app.util.toCompletable
import com.alexz.messenger.app.util.toObservable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import dagger.Binds
import dagger.Component
import dagger.Module
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersProviderImp @Inject constructor() : UsersProvider {

    override val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getNotificationToken(userId: String): Observable<String> =
            usersCollection.document(userId).toObservable { it[NOTIFY_TOKEN].toString() }

    override fun get(id: String): Observable<IUser> =
            usersCollection.document(id).toObservable(User::class.java).map { it as IUser }

    override fun create(entity: IUser): Completable {
        return if (entity.id.isNotEmpty()) {
            usersCollection.document(entity.id).set(entity, SetOptions.merge()).toCompletable()
        } else {
            val doc = usersCollection.document()
            entity.id = doc.id
            doc.set(entity).toCompletable()
        }
    }

    override fun delete(entity: IUser): Completable =
            usersCollection.document(entity.id).delete().toCompletable()

//    override fun findByPhone(vararg phones: String): Observable<List<IUser>> =
//            Observable.fromArray(phones.toList().chunked(10).map { list ->
//                usersCollection.whereIn(PHONE, list).toObservable(User::class.java)
//            })
//                    .flatMapIterable { it.apply{ forEach{ it.observeOn(Schedulers.io()) } }}
//                    .flatMap { it }

    // TODO: 29.06.2021 ИСПРАВИТЬ ЭТУ ЕБУЧУЮ ГРЯЗЬ, КОГДА УЗНАЮ КАК ОБЪЕДИНИТЬ БЛЯДСКИЙ СПИСОК OBSERVABLE в 1 И НИЧЕГО НЕ СЛОМАТь
    override fun findByPhone(vararg phones: String): Observable<List<IUser>> =
            phones.toList().chunked(10).map { list ->
                usersCollection.whereIn(PHONE, list).toObservable(User::class.java).subscribeOn(Schedulers.io())
            }.merge().map {
                it.flatten().toSet().toList() }

//        val disposables = CopyOnWriteArrayList<Disposable>()
//        val users = ConcurrentHashMap<Int, List<IUser>>()
//
//        return Observable.create<List<IUser>> { mainObservable ->
//
//            phones.toList().chunked(10).map { list ->
//                usersCollection.whereIn(PHONE, list).toObservable(User::class.java).subscribeOn(Schedulers.io())
//            }.forEachIndexed { idx, obs->
//                synchronized(disposables) {
//                    disposables.add(obs.subscribe(
//                            { list ->
//                                synchronized(users) {
//                                    users[idx] = list
//                                }
//                                mainObservable.onNext(users.values.flatten().sortedBy { it.name })
//                            },
//                            {
//                                mainObservable.tryOnError(it)
//                            }
//                    ))
//                }
//            }
//        }.doOnDispose {
//            disposables.forEach { it.dispose() }
//        }
//    }



    override fun findByUsername(username: String): Observable<IUser> =
            usersCollection.whereEqualTo(USERNAME, username).toObservable(User::class.java).map { it.first() }

    override fun isUsernameAvailable(username: String): Observable<Boolean> =
//            findByUsername(username).map { it.id == currentUserId }
            usersCollection.whereEqualTo(USERNAME, username).toObservable {
                it[ID].toString()
            }.map { it.isEmpty() || it.first() == currentUserId }
}

@Module
abstract class UsersProviderModule{
    @Binds
    abstract fun usersProvider(usersProvider : UsersProviderImp) : UsersProvider
}

@Singleton
@Component
interface UsersProviderComponent{
    fun getUsersProvider() : UsersProviderImp
}