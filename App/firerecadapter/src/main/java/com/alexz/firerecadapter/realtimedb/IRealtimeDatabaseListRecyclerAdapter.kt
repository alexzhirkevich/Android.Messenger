package com.alexz.firerecadapter.realtimedb

import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.viewholder.IFirebaseViewHolder
import com.google.firebase.database.Query

/**
 * Recycler adapter interface for Firebase Realtime Database objects accessed as list
 *
 * @param Entity object class implements [IEntity]
 * @param VH ViewHolder implements [IFirebaseViewHolder]
 *
 * @see IEntity
 * @see IFirebaseViewHolder
 */
interface IRealtimeDatabaseListRecyclerAdapter<Entity : IEntity, VH : IFirebaseViewHolder<Entity>>
    : IRealtimeDatabaseRecyclerAdapter<Entity, VH> {
    /**
     * @return Firebase [Query] object for Models list
     */
    fun onCreateEntitiesQuery(): Query
}