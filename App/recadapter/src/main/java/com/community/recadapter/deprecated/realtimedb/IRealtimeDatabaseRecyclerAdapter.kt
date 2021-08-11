//package com.community.firerecadapter.realtimedb
//
//import com.community.firerecadapter.IBaseRecyclerAdapter
//import com.community.messenger.common.entities.interfaces.IEntity
//import com.community.firerecadapter.viewholder.IBaseViewHolder
//import com.google.firebase.database.DataSnapshot
//
//interface IRealtimeDatabaseRecyclerAdapter<Entity : com.community.messenger.common.entities.interfaces.IEntity,VH : IBaseViewHolder<Entity>>
//    : IBaseRecyclerAdapter<Entity, VH>{
//
//    /**
//     * Parse Entity object from [DataSnapshot]
//     *
//     * @param snapshot [DataSnapshot]
//     * @return parsed object or null
//     */
//    fun parse(snapshot: DataSnapshot): Entity? //= try {
////        snapshot.getValue(modelClass)
////    }catch (e : Throwable){
////        null
////    }
//}