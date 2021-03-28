package com.alexz.messenger.app.ui.common.firerecyclerview

import android.util.Log
import com.alexz.messenger.app.data.model.interfaces.IBaseModel
import com.google.firebase.database.*

/**
 * Recycler adapter for Firebase Realtime Database objects accessed as list
 *
 * @param modelClass default model class, used to [parse] a [DataSnapshot]
 * @see IBaseModel
 * @see IFirebaseMapRecyclerAdapter
 */
abstract class FirebaseListRecyclerAdapter<Model : IBaseModel, VH : FirebaseViewHolder<Model>>(modelClass: Class<Model>) :
        FirebaseRecyclerAdapter<Model, VH>(modelClass), IFirebaseListRecyclerAdapter<Model, VH>, Listenable {
    private var modelQuery: Query? = null
    private var childEventListener: ChildEventListener? = null
    private var listening = false
    private var chatsCount: Long = -1
    var loading = false

    override fun startListening() {
        if (!listening) {
            listening = true
            if (modelQuery == null) {
                modelQuery = onCreateModelsQuery()
                loading = true
                uiHandler.post { loadingCallback?.onStartLoading() }
                childEventListener = object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val model: Model? = parse(snapshot)
                        if (model != null) {
                            val idx = add(model)
                            notifyItemInserted(idx)
                            adapterCallback?.onItemAdded(model)
                            checkLoadingEnd()
                        }
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        val model: Model? = parse(snapshot)
                        if (model != null) {
                            val idx = add(model)
                            notifyItemChanged(idx)
                            adapterCallback?.onItemChanged(model)
                        }
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        val model: Model? = parse(snapshot)
                        if (model != null) {
                            val removed = remove(model.id)
                            removed?.let {
                                notifyItemRemoved(it.second)
                                adapterCallback?.onItemChanged(it.first)
                            }
                            chatsCount--
                        }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        onChildRemoved(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                }
                modelQuery?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatsCount = snapshot.childrenCount
                        modelQuery = onCreateModelsQuery()
                        childEventListener?.let { modelQuery?.addChildEventListener(it)}
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "ChildEventListener error:\n$error")
                        loadingCallback?.onEndLoading()
                        loading = false
                    }
                })
            } else {
                childEventListener?.let { modelQuery?.addChildEventListener(it)}
            }
        }
    }

    override fun stopListening() {
        if (listening) {
            listening = false
            childEventListener?.let {  modelQuery?.removeEventListener(it)}
        }
    }

    private fun checkLoadingEnd() {
        if (loading && realItemCount() >= chatsCount) {
            loading = false
            uiHandler.post { loadingCallback?.onEndLoading() }
        }
    }

    companion object {
        private val TAG = FirebaseListRecyclerAdapter::class.java.simpleName
    }
}