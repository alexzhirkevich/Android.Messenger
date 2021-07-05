package com.alexz.messenger.app.util

import android.annotation.SuppressLint
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
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

fun <T> DatabaseReference.toObservable(clazz : Class<T>) : Observable<T> = Observable.create {
    addValueEventListener(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.tryOnError(error.toException())
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.getValue(clazz)?.let { v->it.onNext(v) }
        }
    })
}

fun <T> DatabaseReference.toObservable(parser : (DataSnapshot) -> T) : Observable<T> = Observable.create {
    addValueEventListener(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.tryOnError(error.toException())
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            it.onNext(parser(snapshot))
            snapshot.getValue(List::class.java)
        }
    })
}
fun <T> DatabaseReference.toSingle(clazz : Class<T>) : Single<T> = Single.create {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.tryOnError(error.toException())
        }

        @SuppressLint("RestrictedApi")
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.getValue(clazz)?.let { v -> it.onSuccess(v) } ?:
                    it.tryOnError(DatabaseException("Not found"))
        }
    })
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
                it.onNext(qs.map { doc -> parser(doc) })
            } else
                it.onNext(emptyList())
        }
    }
}

fun Task<*>.toCompletable() : Completable = Completable.create {
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