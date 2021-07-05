package com.alexz.firerecadapter.realtimedb

import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.viewholder.IBaseViewHolder
import com.google.firebase.database.Query

/**
 * Recycler adapter interface for Firebase Realtime Database objects accessed as list
 *
 * @param Entity object class implements [IEntity]
 * @param VH ViewHolder implements [IBaseViewHolder]
 *
 * @see IEntity
 * @see IBaseViewHolder
 */
interface IRealtimeDatabaseListRecyclerAdapter<Entity : IEntity, VH : IBaseViewHolder<Entity>>
    : IRealtimeDatabaseRecyclerAdapter<Entity, VH> {
    /**
     * @return Firebase [Query] object for Models list
     */
    fun onCreateEntitiesQuery(): Query
}