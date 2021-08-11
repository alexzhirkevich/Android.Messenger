//package com.community.firerecadapter.firestore
//
//import com.community.firerecadapter.IBaseRecyclerAdapter
//import com.community.firerecadapter.viewholder.IBaseViewHolder
//import com.google.firebase.firestore.DocumentSnapshot
//
//interface IFirestoreRecyclerAdapter<Entity : com.community.messenger.common.entities.interfaces.IEntity,VH : IBaseViewHolder<Entity>>
//    : IBaseRecyclerAdapter<Entity, VH> {
//
//    /**
//     * Parse Entity object from [DocumentSnapshot]
//     *
//     * @param snapshot [DocumentSnapshot] to parse entity object from
//     * @return parsed object or null
//     */
//    fun parse(snapshot: DocumentSnapshot): Entity? //=  try{
////        snapshot.toObject(modelClass)
////    } catch (ignore : Throwable) {
////        null
////    }
//
//    fun sync()
//}