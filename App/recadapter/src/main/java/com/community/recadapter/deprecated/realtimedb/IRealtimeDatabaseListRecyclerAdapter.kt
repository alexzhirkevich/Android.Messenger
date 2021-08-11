//package com.community.firerecadapter.realtimedb
//
//import com.community.firerecadapter.viewholder.IBaseViewHolder
//import com.community.messenger.common.entities.interfaces.IEntity
//import com.google.firebase.database.Query
//
///**
// * Recycler adapter interface for Firebase Realtime Database objects accessed as list
// *
// * @param Entity object class implements [IEntity]
// * @param VH ViewHolder implements [IBaseViewHolder]
// *
// * @see IEntity
// * @see IBaseViewHolder
// */
//interface IRealtimeDatabaseListRecyclerAdapter<Entity : com.community.messenger.common.entities.interfaces.IEntity, VH : IBaseViewHolder<Entity>>
//    : IRealtimeDatabaseRecyclerAdapter<Entity, VH> {
//    /**
//     * @return Firebase [Query] object for Models list
//     */
//    fun onCreateEntitiesQuery(): Query
//}