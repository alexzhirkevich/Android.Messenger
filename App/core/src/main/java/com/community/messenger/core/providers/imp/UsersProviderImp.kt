package com.community.messenger.core.providers.imp

import com.community.messenger.common.entities.imp.User
import com.community.messenger.common.entities.interfaces.IUser
import com.community.messenger.common.util.merge
import com.community.messenger.common.util.toCompletable
import com.community.messenger.common.util.toObservable
import com.community.messenger.core.providers.interfaces.FirebaseProvider
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_NOTIFY_TOKEN
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_PHONE
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_USERNAME
import com.community.messenger.core.providers.interfaces.FirebaseProvider.Companion.FIELD_USERNAME_SEARCH
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersProviderImp @Inject constructor(
        private val firebaseProvider: FirebaseProvider
) : UsersProvider {

    override val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getNotificationToken(userId: String): Observable<String> =
            firebaseProvider.usersCollection.document(userId).toObservable { it[FIELD_NOTIFY_TOKEN].toString() }

    override fun get(id: String): Observable<IUser> =
            firebaseProvider.usersCollection.document(id).toObservable(User::class.java).map { it }

    override fun create(entity: IUser): Completable {
        return if (entity.id.isNotEmpty()) {
            firebaseProvider.usersCollection.document(entity.id).set(entity, SetOptions.merge()).toCompletable()
        } else {
            val doc =  firebaseProvider.usersCollection.document()
            entity.id = doc.id
            doc.set(entity).toCompletable()
        }
    }

    override fun delete(entity: IUser): Completable =
            firebaseProvider.usersCollection.document(entity.id).delete().toCompletable()

//    override fun findByPhone(vararg phones: String): Observable<List<IUser>> =
//            Observable.fromArray(phones.toList().chunked(10).map { list ->
//                usersCollection.whereIn(PHONE, list).toObservable(User::class.java)
//            })
//                    .flatMapIterable { it.apply{ forEach{ it.observeOn(Schedulers.io()) } }}
//                    .flatMap { it }

    // TODO: 29.06.2021 ИСПРАВИТЬ ЭТУ ЕБУЧУЮ ГРЯЗЬ, КОГДА УЗНАЮ КАК ОБЪЕДИНИТЬ БЛЯДСКИЙ СПИСОК
    //  OBSERVABLE в 1 И НИЧЕГО НЕ СЛОМАТь
    override fun findByPhone(vararg phones: String): Observable<Collection<IUser>> =
            phones.toList().chunked(10).map { list ->
                firebaseProvider.usersCollection.whereIn(FIELD_PHONE, list)
                    .toObservable(User::class.java).subscribeOn(Schedulers.io())
            }.merge().map { it.flatten().toSet().toList() }

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
            firebaseProvider.usersCollection.whereEqualTo(FIELD_USERNAME, username)
                .toObservable(User::class.java).map { it.first() }

    override fun findByUsernameNearly(username: String, limit: Int): Observable<Collection<IUser>> {
        var querry = firebaseProvider.usersCollection.whereGreaterThanOrEqualTo(FIELD_USERNAME_SEARCH,username)
        if (limit > -1){
            querry = querry.limitToLast(limit.toLong())
        }
        return querry.toObservable(User::class.java).map { it }
    }

//    override fun isUsernameAvailable(username: String): Observable<Boolean> =
////            findByUsername(username).map { it.id == currentUserId }
//            firebaseProvider.usersCollection.whereEqualTo(FIELD_USERNAME, username).toObservable {
//                it[FIELD_ID].toString()
//            }.map { it.isEmpty() }

}

