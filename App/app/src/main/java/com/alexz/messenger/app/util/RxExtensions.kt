package com.alexz.messenger.app.util

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

fun CompletableEmitter.with(completable : Completable) : Disposable =
    completable.subscribe({onComplete()},{tryOnError(it)})

fun CompletableObserver.with(completable : Completable) : Disposable  =
        completable.subscribe({onComplete()},{onError(it)})

//fun <T> firestoreSingle(
//        task : Task<DocumentSnapshot>,
//        clazz: Class<T>,
//        onParse : (DocumentSnapshot) -> T = { it.toObjectNonNull(clazz) }): Single<T> = Single.create<T> {
//    it.parseObject(task, clazz, onParse)
//}
//
//inline fun <T : Any> firestoreListSingle(
//        task : Task<QuerySnapshot>,
//        clazz: Class<T>,
//        crossinline onParse : (QuerySnapshot) -> List<T> = { it.map { c -> c.toObject(clazz) }}) : Single<List<T>> =
//        Single.create {
//            it.parseList(task,clazz,onParse)
//        }
//fun <T> concatSingleCollections(list : MutableCollection<T> = mutableListOf(),start : Int = 0, vararg source: Single<Collection<T>> )
//        : Single<Collection<T>> = Single.create {
//    if (source.isEmpty() || start >= source.size) {
//        it.onSuccess(list)
//    }
//    source[start]
//            .subscribe(
//                    { col ->
//                        list += col
//                        concatSingleCollections(list, start + 1, *source)
//                                .subscribe(
//                                        { col2 ->
//                                            it.onSuccess(list + col2)
//                                        },
//                                        { t -> it.onError(t) }
//                                )
//                    },{ t -> it.onError(t) }
//            )
//}

//inline fun <T> SingleEmitter<T>.parseObject(
//        task : Task<DocumentSnapshot>,
//        clazz: Class<T>,
//        crossinline onParse : (DocumentSnapshot) -> T? = { it.toObject(clazz) }) =
//        task.addOnSuccessListener {
//            try{
//                onParse(it)?.let { it1 -> onSuccess(it1) }
//            }catch (t : Throwable){
//                tryOnError(t)
//            }
//        }.addOnFailureListener { t ->
//            tryOnError(t)
//        }

//fun <T> SingleEmitter<T>.parseObjectNonNull(
//        task : Task<DocumentSnapshot>,
//        clazz: Class<T>,
//        onParse : (DocumentSnapshot) -> T = { it.toObjectNonNull(clazz) }) =
//        parseObject(task,clazz,onParse)

inline fun <T : Any> SingleEmitter<List<T>>.parseList(
        task : Task<QuerySnapshot>,
        clazz: Class<T>,
        crossinline onParse : (QuerySnapshot) -> List<T> = { it.map { c -> c.toObject(clazz) }}) =
    task.addOnSuccessListener {
        try {
            onSuccess(onParse(it))
        } catch (t: Throwable) {
            tryOnError(t)
        }
    }.addOnFailureListener { t ->
        tryOnError(t)
    }

fun <T : Any> SingleEmitter<List<T>>.parseListNonNull(
        task : Task<QuerySnapshot>,
        clazz: Class<T>,
        onParse : (QuerySnapshot) -> List<T> = { it.mapNotNull { c -> c.toObject(clazz) }}) =
        parseList(task,clazz,onParse)

//inline fun <T> ObservableEmitter<T>.parseObject(
//        reference : DocumentReference,
//        clazz: Class<T>,
//        crossinline onParse : (DocumentSnapshot) -> T? = { it.toObjectNonNull(clazz) }) =
//        reference.addSnapshotListener { ds, error ->
//            if (error != null){
//                tryOnError(error)
//            } else{
//                if (ds != null && ds.exists()){
//                    try{
//                        onParse(ds)?.let { onNext(it) }
//                    }catch (t : Throwable){
//                        tryOnError(t)
//                    }
//                } else{
//                    tryOnError(Exception("DocumentSnapshot is null or doest not exists"))
//                }
//            }
//        }

//inline fun <T : Any> ObservableEmitter<List<T>>.parseList(
//        reference : CollectionReference,
//        clazz: Class<T>,
//        crossinline onParse : (QuerySnapshot) -> List<T>? = { it.toObjects(clazz) }) =
//        reference.addSnapshotListener { qs, error ->
//            if (error != null){
//                tryOnError(error)
//            } else{
//                if (qs != null){
//                    onParse(qs)?.let { onNext(it) }
//                } else{
//                    tryOnError(Exception("Query is null"))
//                }
//            }
//        }

fun <T> Collection<Observable<T>>.merge() : Observable<Collection<T>>{

    val disposables = CopyOnWriteArrayList<Disposable>()
    val items = ConcurrentHashMap<Int, T>()

    return Observable.create<Collection<T>> { mainObservable ->

        this.map { list ->
            list.subscribeOn(Schedulers.io())
        }.forEachIndexed { idx, obs ->
            synchronized(disposables) {
                disposables.add(obs.subscribe(
                        { list ->
                            synchronized(items) {
                                items[idx] = list
                            }
                            mainObservable.onNext(items.values)
                        },
                        {
                            mainObservable.tryOnError(it)
                        }
                ))
            }
        }
    }.doOnDispose {
        disposables.forEach { it.dispose() }
    }
}

fun <T> ObservableEmitter<T>.with(observable: Observable<T>): Disposable =
            observable.subscribe(
                    { t -> onNext(t) },
                    { ex -> tryOnError(ex) },
                    { onComplete() }
            )
