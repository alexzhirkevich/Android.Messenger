package com.community.messenger.common.util

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.*
import io.reactivex.disposables.Disposable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList



fun Completable.invoke() : Disposable {
    return subscribe({},{})
}

fun CompletableEmitter.with(completable : Completable) : Disposable =
    completable.subscribe({onComplete()},{tryOnError(it)})

fun CompletableObserver.with(completable : Completable) : Disposable  =
        completable.subscribe({onComplete()},{onError(it)})

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


fun <T> Collection<Observable<out T>>.merge(waitForAll : Boolean = false) : Observable<out Collection<T>>{

    val disposables = CopyOnWriteArrayList<Disposable>()
    val items = ConcurrentHashMap<Int, T>()

    return Observable.create<Collection<T>> { mainObservable ->

    forEachIndexed { idx, obs ->
            synchronized(disposables) {
                disposables.add(obs.subscribe(
                        { list ->
                            synchronized(items) {
                                items[idx] = list
                            }
                            if (waitForAll && items.size == size || !waitForAll)
                                mainObservable.onNext(items.values)
                        },
                        {
                            mainObservable.tryOnError(it)
                        }
                ))
            }
        }
    }.doFinally {
        disposables.forEach { it.dispose() }
    }
}

fun <T> Observable<out Collection<Observable<out T>>>.toObservableCollection() : Observable< out Collection<T>> {

    return flatMap { it.merge() }

}

fun <T> ObservableEmitter<T>.with(observable: Observable<T>): Disposable =
            observable.subscribe(
                    { t -> onNext(t) },
                    { ex -> tryOnError(ex) },
                    { onComplete() }
            )
