//package com.community.firerecadapter.realtimedb
//
//import android.util.Log
//import com.community.firerecadapter.FirebaseRecyclerAdapter
//import com.community.messenger.common.entities.interfaces.IEntity
//import com.community.firerecadapter.Listenable
//import com.community.recadapter.BaseViewHolder
//import com.google.firebase.database.*
//
///**
// * Recycler adapter for Firebase Realtime Database objects accessed as list
// *
// * @param modelClass default model class, used to [parse] a [DataSnapshot]
// * @see IEntity
// * @see IRealtimeDatabaseListRecyclerAdapter
// */
//abstract class RealtimeDatabaseListRecyclerAdapter<Entity : IEntity, VH : BaseViewHolder<Entity>>(modelClass: Class<Entity>) :
//        FirebaseRecyclerAdapter<Entity, VH>(modelClass),
//        IRealtimeDatabaseListRecyclerAdapter<Entity, VH>, Listenable {
//
//    private var entitiesQuery: Query? = null
//    private var childEventListener: ChildEventListener? = null
//    private var entitiesCount = -1L
//
//    override fun startListening() {
//        if (!isListen) {
//            isListen = true
//            if (entitiesQuery == null) {
//                entitiesQuery = onCreateEntitiesQuery()
//                isLoading = true
//                uiHandler.post { loadingCallback?.onStartLoading() }
//                childEventListener = object : ChildEventListener {
//                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                        val model: Entity? = parse(snapshot)
//                        model?.let {
//                            add(it)
//                            checkLoadingEnd()
//                        }
//                    }
//
//                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                        val model: Entity? = parse(snapshot)
//                        model?.let { add(it) }
//                    }
//
//                    override fun onChildRemoved(snapshot: DataSnapshot) {
//                        val model: Entity? = parse(snapshot)
//                        model?.let {
//                            remove(it.id)
//                            entitiesCount--
//                        }
//                    }
//
//                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//                        onChildRemoved(snapshot)
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {}
//                }
//
//                entitiesQuery?.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        entitiesCount = snapshot.childrenCount
//                        entitiesQuery = onCreateEntitiesQuery()
//                        childEventListener?.let { entitiesQuery?.addChildEventListener(it)}
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        loadingCallback?.onEndLoading()
//                        isLoading = false
//                        Log.e(TAG, "ChildEventListener error:\n$error")
//                    }
//                })
//            } else {
//                childEventListener?.let { entitiesQuery?.addChildEventListener(it)}
//            }
//        }
//    }
//
//    override fun stopListening() {
//        if (isListen) {
//            isListen = false
//            childEventListener?.let {  entitiesQuery?.removeEventListener(it)}
//        }
//    }
//
//    private fun checkLoadingEnd() {
//        if (isLoading && realItemCount() >= entitiesCount) {
//            isLoading = false
//            uiHandler.post { loadingCallback?.onEndLoading() }
//        }
//    }
//
//    companion object {
//        private val TAG = RealtimeDatabaseListRecyclerAdapter::class.java.simpleName
//    }
//}