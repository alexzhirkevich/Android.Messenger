package com.alexz.messenger.app.ui.common.firerecyclerview

import android.util.Log
import com.alexz.messenger.app.data.model.interfaces.IBaseModel
import com.google.firebase.database.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Recycler adapter for Firebase Realtime Database objects accessed by key
 *
 * @param modelClass default model class, used to [parse] a [DataSnapshot]
 * @see IBaseModel
 * @see IFirebaseMapRecyclerAdapter
 */
abstract class FirebaseMapRecyclerAdapter<Model : IBaseModel, VH : FirebaseViewHolder<Model>>(modelClass: Class<Model>) :
        FirebaseRecyclerAdapter<Model, VH>(modelClass),
        IFirebaseMapRecyclerAdapter<Model, VH>,
        Listenable {

    private var keysQuery: Query? = null
    private val modelsInfo: MutableMap<String, ObservableModelInfo> = ConcurrentHashMap()
    private var keysQueryListener: ChildEventListener? = null
    private var isListen = false
    private var isLoading = false
    private var modelsCnt: Long = -1

    /**
     * @see Listenable.startListening
     */
    override fun startListening() {
        if (isListen) {
            return
        }

        isListen = true
        if (keysQueryListener == null) {
            uiHandler.post { loadingCallback?.onStartLoading() }
            isLoading = true
            keysQueryListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.key?.let { observeModel(it) }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.key != null && previousChildName != null && snapshot.key != previousChildName) {
                        stopObserve(previousChildName)
                    }
                    snapshot.key?.let { observeModel(it) }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    snapshot.key?.let {
                        stopObserve(it)
                        modelsCnt--
                        checkLoadingEnd()
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot.key?.let { stopObserve(it) }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to get keys")
                }
            }
            keysQuery = onCreateKeyQuery()
            keysQuery?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        modelsCnt = snapshot.childrenCount
                        keysQueryListener?.let { keysQuery?.addChildEventListener(it) }
                        Log.w(TAG, "Loading $modelsCnt chats")
                        if (modelsCnt == 0L) {
                            uiHandler.post { loadingCallback?.onEndLoading() }
                        }
                    } else {
                        uiHandler.post { loadingCallback?.onEndLoading() }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to get keys count")
                    uiHandler.post { loadingCallback?.onEndLoading() }
                }
            })
        } else {
            keysQueryListener?.let { keysQuery?.addChildEventListener(it) }
            modelsInfo.forEach { it.value.startListening() }
            keysQueryListener?.let { keysQuery?.addChildEventListener(it) }
        }
    }

    /**
     * @see Listenable.stopListening
     */
    override fun stopListening() {
        if (isListen) {
            isListen = false
            keysQueryListener?.let { keysQuery?.removeEventListener(it)}
            modelsInfo.forEach{ it.value.stopListening() }
        }
    }

    private fun observeModel(modelId: String) = synchronized(modelsInfo) {
        if (modelsInfo.containsKey(modelId) && isListen) {
            modelsInfo[modelId]?.startListening()
        } else {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        onModelNotFound(modelId)
                        Log.w(TAG, "Failed to observe model. Id: $modelId")
                    }

                    val newModel = parse(snapshot) ?: return
                    val lastIdx = realItemCount();
                    val idx = add(newModel)
                    val isChanged = idx <= lastIdx
                    if (!isSearching) {
                        if (isChanged) {
                            notifyItemInserted(idx)
                        } else {
                            notifyItemChanged(idx)
                        }
                    }
                    adapterCallback?.let {
                        uiHandler.post {
                            if (isChanged)
                                it.onItemChanged(newModel)
                            else
                                it.onItemAdded(newModel)
                        }
                    }
                    checkLoadingEnd()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "Failed to observe model. Id: $modelId")
                }
            }
            val modelInfo = ObservableModelInfo(onCreateModelQuery(modelId), valueEventListener)
            modelsInfo[modelId] = modelInfo
            if (isListen) {
                modelInfo.startListening()
            } else Unit
        }
    }


    private fun stopObserve(modelId: String) {
        synchronized(modelsInfo) {
            modelsInfo.remove(modelId)?.stopListening() ?: return
        }
        remove(modelId)?.let {
            uiHandler.post { adapterCallback?.onItemRemoved(it.first) }
            if (!isSearching) {
                notifyItemRemoved(it.second)
            }
        }
    }

    private fun checkLoadingEnd() {
        if (isLoading && realItemCount() >= modelsCnt) {
            isLoading = false
            uiHandler.post { loadingCallback?.onEndLoading() }
        }
    }

    private class ObservableModelInfo(private val query : Query, private val listener: ValueEventListener) : Listenable {
        private var isListening = false

        override fun startListening() {
            if (!isListening) {
                isListening = true
                query.addValueEventListener(listener)
            }
        }

        override fun stopListening() {
            if (isListening) {
                isListening = false
                query.removeEventListener(listener)
            }
        }
    }

    companion object {
        private val TAG = FirebaseMapRecyclerAdapter::class.java.simpleName
    }
}