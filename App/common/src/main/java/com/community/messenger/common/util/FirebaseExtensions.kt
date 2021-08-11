package com.community.messenger.common.util

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

class SnapshotNotFoundException(val id : String) : Exception("Snapshot not found. id = $id")


//fun <T> DocumentSnapshot.toObjectNonNull(clazz : Class<T>) : T =
//        toObject(clazz) ?: throw SnapshotNotFoundException("Parsing error")

fun <T> DocumentReference.toObservable(clazz : Class<T>) : Observable<T>{
    var reg: ListenerRegistration? = null
    return Observable.create<T> {
        reg = addSnapshotListener { ds, error ->
            if (error != null) {
                it.tryOnError(error)
                reg?.remove()
            } else {
                ds?.toObject(clazz)?.let { v-> it.onNext(v) } ?: it.tryOnError(SnapshotNotFoundException(id))
            }
        }
    }.doFinally { reg?.remove() }
}

fun <T> DatabaseReference.toObservable(clazz : Class<T>) : Observable<T> {
    var l : ValueEventListener?= null
    return  Observable.create<T> {
        l = addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                it.tryOnError(error.toException())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(clazz)?.let { v->it.onNext(v) } ?:
                    it.tryOnError(SnapshotNotFoundException(key.orEmpty()))
            }
        })
    }.doFinally {
        l?.let { removeEventListener(it) }
    }
}

fun <T> DatabaseReference.toObservable(parser : (DataSnapshot) -> T) : Observable<T> = Observable.create {
    addValueEventListener(object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            it.tryOnError(error.toException())
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            it.onNext(parser(snapshot)!!)
            snapshot.getValue(List::class.java)
        }
    })
}
fun <T:Any> DatabaseReference.toSingle(clazz : Class<T>) : Single<T> = Single.create {
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

fun <T : Any> DocumentReference.toObservable(
        parser : (DocumentSnapshot) -> T
) : Observable<T> = Observable.create {
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
                it.tryOnError(SnapshotNotFoundException("Failed to observe entity. DocumentSnapshot is null"))
                reg?.remove()
            }
        }
    }
}

fun <T:Any> Task<DocumentSnapshot>.toSingle(clazz: Class<T>) : Single<T> = Single.create {
    addOnSuccessListener { v -> it.onSuccess(v.toObject(clazz)!!) }
    addOnFailureListener { t -> it.tryOnError(t) }
    addOnCanceledListener {  }
}

fun <T:Any> Task<DocumentSnapshot>.toSingle(parser: (Map<String,Any?>) -> T) : Single<T> = Single.create {
    addOnSuccessListener { v -> it.onSuccess(parser(v.data!!)) }
    addOnFailureListener { t -> it.tryOnError(t) }
    addOnCanceledListener {  }
}

fun <T:Any> Task<T>.toSingle() : Single<T> = Single.create<T> {
    addOnSuccessListener { v -> it.onSuccess(v) }
    addOnFailureListener { t -> it.tryOnError(t) }
    addOnCanceledListener {  }
}

fun <T> Query.toObservable(clazz : Class<T>) : Observable<Collection<T>>  {
    var reg : ListenerRegistration?=null
    return Observable.create<Collection<T>> {
        reg = addSnapshotListener { qs, error ->
            if (error != null) {
                it.tryOnError(error)
            } else {
                if (qs != null) {
                    try {
                        it.onNext(qs.toObjects(clazz).mapNotNull { it })
                    } catch (t: Throwable) {
                        it.tryOnError(t)
                    }
                } else
                    it.onNext(emptyList())
            }
        }
    }.doFinally { reg?.remove() }
}

fun <T> Query.toObservable(parser: (DocumentSnapshot) -> T) : Observable<Collection<T>> {
    var reg : ListenerRegistration?=null
    return Observable.create<Collection<T>> {
        reg = addSnapshotListener { qs, error ->
            if (error != null) {
                it.tryOnError(error)
            } else {
                if (qs != null) {
                    it.onNext(qs.map { doc -> parser(doc) })
                } else
                    it.onNext(emptyList())
            }
        }
    }.doOnDispose { reg?.remove() }
}

fun Task<*>.toCompletable() : Completable = Completable.create {
    addOnSuccessListener {_ ->
        it.onComplete()
    }.addOnFailureListener { t -> it.tryOnError(t) }
}

fun <T> Task<DocumentSnapshot>.toMaybe(clazz : Class<T>) = Maybe.create<T> {
    addOnSuccessListener { ds ->
        ds.toObject(clazz)?.let { t -> it.onSuccess(t) } ?: it.onComplete()
    }.addOnFailureListener { t -> it.tryOnError(t) }
}