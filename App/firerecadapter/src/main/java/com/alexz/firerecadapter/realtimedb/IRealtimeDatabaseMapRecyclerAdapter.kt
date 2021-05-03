package com.alexz.firerecadapter.realtimedb

import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.viewholder.IFirebaseViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.Query

/**
 * Recycler adapter interface for Firebase Realtime Database objects accessed by key.
 * @param Entity object class implements [IEntity]
 * @param VH ViewHolder implements [IFirebaseViewHolder]
 *
 * @see IEntity
 * @see IFirebaseViewHolder
 */
interface IRealtimeDatabaseMapRecyclerAdapter<Entity : IEntity, VH : IFirebaseViewHolder<Entity>>
    : IRealtimeDatabaseRecyclerAdapter<Entity, VH> {
    /**
     * @return Firebase [Query] object for Entities key set ([IEntity.id] - key)
     */
    fun onCreateKeyQuery(): Query

    /**
     * @return Firebase [Query] object for Entity by key
     * @param id key ([IEntity.id])
     */
    fun onCreateEntityQuery(id: String): Query

    /**
     * Called when entity DataSnapshot, got by key, not exists (cannot find object in DB)
     * @see DataSnapshot
     */
    fun onEntityNotFound(id: String) {}
}