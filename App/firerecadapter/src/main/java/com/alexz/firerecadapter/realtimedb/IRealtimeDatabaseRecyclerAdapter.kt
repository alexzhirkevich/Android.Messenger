package com.alexz.firerecadapter.realtimedb

import com.alexz.firerecadapter.IBaseRecyclerAdapter
import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.viewholder.IFirebaseViewHolder
import com.google.firebase.database.DataSnapshot

interface IRealtimeDatabaseRecyclerAdapter<Entity : IEntity,VH : IFirebaseViewHolder<Entity>>
    : IBaseRecyclerAdapter<Entity, VH>{

    /**
     * Parse Entity object from [DataSnapshot]
     *
     * @param snapshot [DataSnapshot]
     * @return parsed object or null
     */
    fun parse(snapshot: DataSnapshot): Entity? //= try {
//        snapshot.getValue(modelClass)
//    }catch (e : Throwable){
//        null
//    }
}