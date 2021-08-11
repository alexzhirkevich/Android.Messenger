//package com.community.firerecadapter.realtimedb
//
//import com.community.messenger.common.entities.interfaces.IEntity
//import com.community.firerecadapter.viewholder.IBaseViewHolder
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.Query
//
///**
// * Recycler adapter interface for Firebase Realtime Database objects accessed by key.
// * @param Entity object class implements [IEntity]
// * @param VH ViewHolder implements [IBaseViewHolder]
// *
// * @see IEntity
// * @see IBaseViewHolder
// */
//interface IRealtimeDatabaseMapRecyclerAdapter<Entity : com.community.messenger.common.entities.interfaces.IEntity, VH : IBaseViewHolder<Entity>>
//    : IRealtimeDatabaseRecyclerAdapter<Entity, VH> {
//    /**
//     * @return Firebase [Query] object for Entities key set ([IEntity.id] - key)
//     */
//    fun onCreateKeyQuery(): Query
//
//    /**
//     * @return Firebase [Query] object for Entity by key
//     * @param id key ([IEntity.id])
//     */
//    fun onCreateEntityQuery(id: String): Query
//
//    /**
//     * Called when entity DataSnapshot, got by key, not exists (cannot find object in DB)
//     * @see DataSnapshot
//     */
//    fun onEntityNotFound(id: String) {}
//}