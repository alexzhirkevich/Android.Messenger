package com.alexz.firerecadapter.firestore

import com.alexz.firerecadapter.IBaseRecyclerAdapter
import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.viewholder.IBaseViewHolder
import com.google.firebase.firestore.DocumentSnapshot

interface IFirestoreRecyclerAdapter<Entity : IEntity,VH : IBaseViewHolder<Entity>>
    : IBaseRecyclerAdapter<Entity, VH> {

    /**
     * Parse Entity object from [DocumentSnapshot]
     *
     * @param snapshot [DocumentSnapshot] to parse entity object from
     * @return parsed object or null
     */
    fun parse(snapshot: DocumentSnapshot): Entity? //=  try{
//        snapshot.toObject(modelClass)
//    } catch (ignore : Throwable) {
//        null
//    }

    fun sync()
}