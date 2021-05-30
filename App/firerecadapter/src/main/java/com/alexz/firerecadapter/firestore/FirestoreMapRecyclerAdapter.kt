package com.alexz.firerecadapter.firestore

import android.util.Log
import androidx.annotation.CallSuper
import com.alexz.firerecadapter.FirebaseRecyclerAdapter
import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.Listenable
import com.alexz.firerecadapter.viewholder.FirebaseViewHolder
import com.alexz.firestorerecadapter.IFirestoreMapRecyclerAdapter
import com.google.firebase.firestore.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Recycler adapter for Firebase Firestore objects accessed by key
 *
 * @param clazz default entity class, used to [parse] a [DocumentSnapshot]
 * @see IEntity
 * @see IFirestoreMapRecyclerAdapter
 */
abstract class FirestoreMapRecyclerAdapter<Entity : IEntity, VH : FirebaseViewHolder<Entity>>(
        clazz: Class<Entity>,
        override val keyCollectionReference: CollectionReference) :
        FirebaseRecyclerAdapter<Entity, VH>(clazz),
        IFirestoreMapRecyclerAdapter<Entity, VH>, Listenable {

    private val modelsInfo: MutableMap<String, ObservableModelInfo> = ConcurrentHashMap()
    private var modelsCount: Int = -1
    private var reg : ListenerRegistration? = null

    /**
     * @see Listenable.startListening
     */
    @CallSuper
    override fun startListening() = if (!isListen) {
        isListen = true
        onStartLoading()
        reg = keyCollectionReference.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Failed to get keys\n" + error.message.orEmpty())
                onEndLoading()
            } else {
                modelsCount = snapshot?.size() ?: -1
                if (modelsCount <=0){
                    onEndLoading()
                }
                snapshot?.documentChanges?.forEach { dc ->
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> observe(dc.document.id)
                        DocumentChange.Type.REMOVED -> removeObserver(dc.document.id)
                        DocumentChange.Type.MODIFIED -> removeObserver(dc.document.id)
                                .also { observe(dc.document.id) }
                    }
                }
            }
        }
    } else Unit

    /**
     * @see Listenable.stopListening
     */
    @CallSuper
    override fun stopListening() {
        if (isListen) {
            isListen = false
            reg?.remove()
            modelsInfo.forEach{ it.value.stopListening() }
        }
    }

    private fun observe(id: String) = synchronized(modelsInfo) {
        if (modelsInfo.containsKey(id) && isListen) {
            modelsInfo[id]?.startListening()
        } else {
            val valueEventListener = object : EventListener<DocumentSnapshot> {

                override fun onEvent(snapshot: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){
                        Log.w(TAG, "Failed to observe model. Id: $id. ${error.message}")
                        return
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val model = parse(snapshot) ?:return
                        add(model)
                        if (isLoading && realItemCount() >= modelsCount) {
                           onEndLoading()
                        }
                    }
                }
            }
            val modelInfo = ObservableModelInfo(onCreateEntityReference(id), valueEventListener)
            modelsInfo[id] = modelInfo
            if (isListen) {
                modelInfo.startListening()
            } else Unit
        }
    }

    private fun removeObserver(id: String) {
        synchronized(modelsInfo) {
            modelsInfo.remove(id)?.stopListening() ?: return
        }
        remove(id)
    }

    private class ObservableModelInfo(
            private val query : DocumentReference,
            private val listener: EventListener<DocumentSnapshot>) :  Listenable {

        private var reg : ListenerRegistration? = null

        override fun startListening() {
            if (reg == null) {
                reg = query.addSnapshotListener(listener)
            }
        }

        override fun stopListening() {
            if (reg != null) {
                reg?.remove()
                reg = null
            }
        }
    }

    companion object {
        private val TAG = FirestoreMapRecyclerAdapter::class.java.simpleName
    }
}