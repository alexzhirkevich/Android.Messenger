package com.alexz.messenger.app.util

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable


fun taskCompletable(task : Task<*>): Completable = Completable.create {
    it.complete(task)
}

fun CompletableEmitter.complete(task : Task<*>) =
        task.addOnSuccessListener {
            onComplete()
        }.addOnFailureListener { tryOnError(it) }

fun CompletableObserver.complete(task : Task<*>) =
        task.addOnSuccessListener {
            onComplete()
        }.addOnFailureListener { onError(it) }


fun CompletableEmitter.with(completable : Completable) : Disposable  =
    completable.doOnComplete {
        onComplete()
    }.doOnError {
        tryOnError(it)
    }.subscribe()

fun CompletableObserver.with(completable : Completable) : Disposable  =
        completable.subscribe({onComplete()},{onError(it)})

fun <T> firestoreSingle(
        task : Task<DocumentSnapshot>,
        clazz: Class<T>,
        onParse : (DocumentSnapshot) -> T = { it.toObjectNonNull(clazz) }): Single<T> = Single.create<T> {
    it.parseObject(task, clazz, onParse)
}

inline fun <T : Any> firestoreListSingle(
        task : Task<QuerySnapshot>,
        clazz: Class<T>,
        crossinline onParse : (QuerySnapshot) -> List<T> = { it.map { c -> c.toObject(clazz) }}) : Single<List<T>> =
        Single.create {
            it.parseList(task,clazz,onParse)
        }
fun <T> concatSingleCollections(list : MutableCollection<T> = mutableListOf(),start : Int = 0, vararg source: Single<Collection<T>> )
        : Single<Collection<T>> = Single.create {
    if (source.isEmpty() || start >= source.size) {
        it.onSuccess(list)
    }
    source[start]
            .doOnSuccess { col ->
                list += col
                concatSingleCollections(list, start + 1, *source)
                        .subscribe(
                                { col2 ->
                                    it.onSuccess(list + col2)
                                },
                                { t -> it.onError(t) }
                        )
            }
            .doOnError { t -> it.onError(t) }
            .subscribe()
}

inline fun <T> SingleEmitter<T>.parseObject(
        task : Task<DocumentSnapshot>,
        clazz: Class<T>,
        crossinline onParse : (DocumentSnapshot) -> T? = { it.toObject(clazz) }) =
        task.addOnSuccessListener {
            try{
                onSuccess(onParse(it))
            }catch (t : Throwable){
                tryOnError(t)
            }
        }.addOnFailureListener { t ->
            tryOnError(t)
        }

fun <T> SingleEmitter<T>.parseObjectNonNull(
        task : Task<DocumentSnapshot>,
        clazz: Class<T>,
        onParse : (DocumentSnapshot) -> T = { it.toObjectNonNull(clazz) }) =
        parseObject(task,clazz,onParse)

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




inline fun <T> firestoreObservable(
        reference : DocumentReference,
        clazz: Class<T>,
        crossinline onParse : (DocumentSnapshot) -> T? = { it.toObject(clazz) }): Observable<T> = Observable.create<T> {
        it.parseObject(reference,clazz,onParse)
}

inline fun <T> ObservableEmitter<T>.parseObject(
        reference : DocumentReference,
        clazz: Class<T>,
        crossinline onParse : (DocumentSnapshot) -> T? = { it.toObject(clazz) }) =
        reference.addSnapshotListener { ds, error ->
            if (error != null){
                tryOnError(error)
            } else{
                if (ds != null && ds.exists()){
                    try{
                        onParse(ds)?.let { onNext(it) }
                    }catch (t : Throwable){
                        tryOnError(t)
                    }
                }
            }
        }

fun <T> ObservableEmitter<T>.with(observable: Observable<T>): Disposable =
            observable.subscribe(
                    { t -> onNext(t) },
                    { ex -> tryOnError(ex) },
                    { onComplete() }
            )
