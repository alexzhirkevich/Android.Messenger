package com.alexz.messenger.app.util

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.CancellationException


fun <T> DocumentSnapshot.toObjectNonNull(clazz : Class<T>) : T =
        toObject(clazz) ?: throw ClassCastException("Parsing error")

fun <T> DocumentReference.toObservable(clazz : Class<T>) : Observable<T> = Observable.create<T> {
    var reg: ListenerRegistration? = null
    reg = addSnapshotListener { ds, error ->
        if (error != null) {
            it.tryOnError(error)
            reg?.remove()
        } else {
            if (ds != null) {
                try {
                    it.onNext(ds.toObjectNonNull(clazz))
                } catch (t: Throwable) {
                    it.tryOnError(t)
                    reg?.remove()
                }
            } else {
                it.tryOnError(NullPointerException("Failed to observe entity. DocumentSnapshot is null"))
                reg?.remove()
            }
        }
    }
}

fun <T> DocumentReference.toObservable(
        parser : (DocumentSnapshot) -> T
) : Observable<T> = Observable.create<T> {
    var reg: ListenerRegistration? = null
    reg = addSnapshotListener { ds, error ->
        if (error != null) {
            it.tryOnError(error)
            reg?.remove()
        } else {
            if (ds != null) {
                try {
                    it.onNext(parser(ds))
                } catch (t: Throwable) {
                    it.tryOnError(t)
                    reg?.remove()
                }
            } else {
                it.tryOnError(NullPointerException("Failed to observe entity. DocumentSnapshot is null"))
                reg?.remove()
            }
        }
    }
}

fun <T> Task<T>.toSingle() : Single<T> = Single.create<T> {
    addOnSuccessListener { v -> it.onSuccess(v) }
    addOnFailureListener { t -> it.tryOnError(t) }
    addOnCanceledListener { it.tryOnError(CancellationException("Canceled")) }
}

fun <T> Query.toObservable(clazz : Class<T>) : Observable<List<T>> = Observable.create {
    addSnapshotListener { qs, error ->
        if (error != null) {
            it.tryOnError(error)
        } else {
            if (qs != null) {
                try {
                    it.onNext(qs.toObjects(clazz))
                } catch (t: Throwable) {
                    it.tryOnError(t)
                }
            } else
                it.onNext(emptyList())
        }
    }
}

fun <T> Query.toObservable(parser: (DocumentSnapshot) -> T) : Observable<List<T>> = Observable.create {
    addSnapshotListener { qs, error ->
        if (error != null) {
            it.tryOnError(error)
        } else {
            if (qs != null) {
                try {
                    it.onNext(qs.map { doc -> parser(doc) })
                } catch (t: Throwable) {
                    it.tryOnError(t)
                }
            } else
                it.onNext(emptyList())
        }
    }
}

fun Task<Void>.toCompletable() : Completable = Completable.create {
    addOnSuccessListener {_ ->
        it.onComplete()
    }.addOnFailureListener { t -> it.tryOnError(t) }
}

fun <T> Task<DocumentSnapshot>.toMaybe(clazz : Class<T>) = Maybe.create<T> {
    addOnSuccessListener { ds ->
        if (ds.exists()) {
            ds.toObject(clazz)?.let { t -> it.onSuccess(t) } ?: it.onComplete()
        } else {
            it.tryOnError(Exception("Document not exists"))
        }
    }.addOnFailureListener { t -> it.tryOnError(t) }
}