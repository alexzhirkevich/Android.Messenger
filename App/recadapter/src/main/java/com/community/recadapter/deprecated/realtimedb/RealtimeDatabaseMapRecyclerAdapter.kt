//package com.community.firerecadapter.realtimedb
//
//import android.util.Log
//import com.community.firerecadapter.FirebaseRecyclerAdapter
//import com.community.messenger.common.entities.interfaces.IEntity
//import com.community.firerecadapter.Listenable
//import com.community.recadapter.BaseViewHolder
//import com.google.firebase.database.*
//import java.util.concurrent.ConcurrentHashMap
//
///**
// * Recycler adapter for Firebase Realtime Database objects accessed by key
// *
// * @param clazz default entity class, used to [parse] a [DataSnapshot]
// * @see IEntity
// * @see IRealtimeDatabaseMapRecyclerAdapter
// */
//abstract class RealtimeDatabaseMapRecyclerAdapter<Entity : IEntity, VH : BaseViewHolder<Entity>>(clazz: Class<Entity>) :
//        FirebaseRecyclerAdapter<Entity, VH>(clazz),
//        IRealtimeDatabaseMapRecyclerAdapter<Entity, VH>, Listenable {
//
//    private var keysQuery: Query? = null
//    private val entitiesInfo: MutableMap<String, ObservableModelInfo> = ConcurrentHashMap()
//    private var keysQueryListener: ChildEventListener? = null
//    private var entitiesCount: Long = -1
//
//    /**
//     * @see Listenable.startListening
//     */
//    override fun startListening() {
//
//        if (isListen) {
//            return
//        }
//
//        isListen = true
//        if (keysQueryListener == null) {
//            onStartLoading()
//            keysQueryListener = object : ChildEventListener {
//                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                    snapshot.key?.let { observe(it) }
//                }
//
//                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                    if (snapshot.key != null && previousChildName != null && snapshot.key != previousChildName) {
//                        removeObserver(previousChildName)
//                    }
//                    snapshot.key?.let { observe(it) }
//                }
//
//                override fun onChildRemoved(snapshot: DataSnapshot) {
//                    snapshot.key?.let {
//                        removeObserver(it)
//                        entitiesCount--
//                        checkLoadingEnd()
//                    }
//                }
//
//                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                    snapshot.key?.let { removeObserver(it) }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.w(TAG, "Failed to get keys")
//                }
//            }
//            keysQuery = onCreateKeyQuery()
//            keysQuery?.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        entitiesCount = snapshot.childrenCount
//                        keysQueryListener?.let { keysQuery?.addChildEventListener(it) }
//                        Log.w(TAG, "Loading $entitiesCount chats")
//                    }
//                    if (isLoading && entitiesCount <= 0L) {
//                        onEndLoading()
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.w(TAG, "Failed to get keys count")
//                    onEndLoading()
//                }
//            })
//        } else {
//            keysQueryListener?.let { keysQuery?.addChildEventListener(it) }
//            entitiesInfo.forEach { it.value.startListening() }
//            keysQueryListener?.let { keysQuery?.addChildEventListener(it) }
//        }
//    }
//
//    /**
//     * @see Listenable.stopListening
//     */
//    override fun stopListening() {
//        if (isListen) {
//            isListen = false
//            keysQueryListener?.let { keysQuery?.removeEventListener(it)}
//            entitiesInfo.forEach{ it.value.stopListening() }
//        }
//    }
//
//    private fun observe(id: String) = synchronized(entitiesInfo) {
//        if (entitiesInfo.containsKey(id) && isListen) {
//            entitiesInfo[id]?.startListening()
//        } else {
//            val valueEventListener = object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (!snapshot.exists()) {
//                        onEntityNotFound(id)
//                        Log.w(TAG, "Failed to observe model. Id: $id")
//                    }
//
//                    val newModel = parse(snapshot) ?: return
//                    add(newModel)
//                    checkLoadingEnd()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.w(TAG, "Failed to observe model. Id: $id")
//                }
//            }
//            val modelInfo = ObservableModelInfo(onCreateEntityQuery(id), valueEventListener)
//            entitiesInfo[id] = modelInfo
//            if (isListen) {
//                modelInfo.startListening()
//            } else Unit
//        }
//    }
//
//    private fun removeObserver(id: String) {
//        synchronized(entitiesInfo) {
//            entitiesInfo.remove(id)?.stopListening() ?: return
//        }
//        remove(id)
//    }
//
//    private fun checkLoadingEnd() {
//        if (isLoading && realItemCount() >= entitiesCount) {
//            isLoading = false
//            uiHandler.post { loadingCallback?.onEndLoading() }
//        }
//    }
//
//    private class ObservableModelInfo(private val query : Query, private val listener: ValueEventListener) :
//            Listenable {
//        private var isListening = false
//
//        override fun startListening() {
//            if (!isListening) {
//                isListening = true
//                query.addValueEventListener(listener)
//            }
//        }
//
//        override fun stopListening() {
//            if (isListening) {
//                isListening = false
//                query.removeEventListener(listener)
//            }
//        }
//    }
//
//    companion object {
//        private val TAG = RealtimeDatabaseListRecyclerAdapter::class.java.simpleName
//    }
//}