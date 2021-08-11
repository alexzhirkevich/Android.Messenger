//package com.community.firerecadapter.firestore
//
//import com.community.messenger.common.entities.interfaces.IEntity
//import com.community.firerecadapter.viewholder.IBaseViewHolder
//import com.google.firebase.firestore.CollectionReference
//
///**
// * Recycler adapter interface for Firebase Firestore objects accessed as collection
// *
// * @param Entity object class implements [IEntity]
// * @param VH ViewHolder implements [IBaseViewHolder]
// *
// * @see IEntity
// * @see IBaseViewHolder
// */
//interface IFirestoreListRecyclerAdapter<Entity : com.community.messenger.common.entities.interfaces.IEntity, VH : IBaseViewHolder<Entity>>
//    :IFirestoreRecyclerAdapter<Entity,VH>{
//
//    val entityCollectionReference : CollectionReference
//}