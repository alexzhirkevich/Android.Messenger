package com.alexz.firestorerecadapter

import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.firestore.IFirestoreRecyclerAdapter
import com.alexz.firerecadapter.viewholder.IBaseViewHolder
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

/**
 * Recycler adapter interface for Firebase Realtime Database objects accessed by key.
 * @param Entity object class implements [IEntity]
 * @param VH ViewHolder implements [IBaseViewHolder]
 *
 * @see IEntity
 * @see IBaseViewHolder
 */
interface IFirestoreMapRecyclerAdapter<Entity : IEntity, VH : IBaseViewHolder<Entity>>
    : IFirestoreRecyclerAdapter<Entity,VH>{
    /**
     * @return Firebase [Query] object for Models key set ([IEntity.id] - key)
     */
    val keyCollectionReference : CollectionReference

    /**
     * @return Firebase [Query] object for Entity by key
     * @param id key ([IEntity.id])
     */
    fun onCreateEntityReference(id: String): DocumentReference

    /**
     * Called when entity DataSnapshot, got by key, not exists (cannot find object in DB)
     * @see DataSnapshot
     */
    fun onEntityNotFound(id: String) {}
}