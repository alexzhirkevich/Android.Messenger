//package com.community.firerecadapter.firestore
//
//import android.util.Log
//import androidx.annotation.CallSuper
//import com.community.firerecadapter.FirebaseRecyclerAdapter
//import com.community.messenger.common.entities.interfaces.IEntity
//import com.community.firerecadapter.Listenable
//import com.community.recadapter.BaseViewHolder
//import com.google.firebase.firestore.*
//
///**
// * Recycler adapter for Firebase Firestore objects accessed as collection
// *
// * @param clazz default model class, used to [parse] a [DocumentSnapshot]
// * @see IEntity
// * @see IFirestoreListRecyclerAdapter
// */
//abstract class FirestoreListRecyclerAdapter<Entity : IEntity, VH : BaseViewHolder<Entity>>(
//        clazz: Class<Entity>, override val entityCollectionReference : CollectionReference) :
//        FirebaseRecyclerAdapter<Entity, VH>(clazz),
//        IFirestoreListRecyclerAdapter<Entity, VH> , Listenable {
//
//    private var listenerRegistration: ListenerRegistration? = null
//
//    @CallSuper
//    override fun startListening() = if (!isListen) {
//        isListen = true
//        onStartLoading()
//        listenerRegistration = entityCollectionReference
//                .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
//                    if (error != null) {
//                        Log.e(TAG, "Error while getting data.\n${error.stackTrace}")
//                        onEndLoading()
//                    } else {
//                        value?.documentChanges?.forEach { ch ->
//                            when (ch.type) {
//                                DocumentChange.Type.ADDED -> {
//                                    parse(ch.document)?.let {
//                                        add(it,byUser = false)
//                                    }
//                                }
//                                DocumentChange.Type.REMOVED -> {
//                                    parse(ch.document)?.let {
//                                        remove(it.id,byUser = false)
//                                    } ?: remove(ch.document.id,byUser = false)
//                                }
//                                DocumentChange.Type.MODIFIED -> {
//                                    parse(ch.document)?.let { add(it,byUser = false) }
//                                }
//                            }
//                        }
//                        onEndLoading()
//                    }
//                }
//    } else Unit
//
//    @CallSuper
//    override fun stopListening() = if (isListen) {
//        isListen = false
//        listenerRegistration?.remove()
//        listenerRegistration = null
//    } else Unit
//
//    companion object {
//        private val TAG = FirestoreListRecyclerAdapter::class.java.simpleName
//    }
//}