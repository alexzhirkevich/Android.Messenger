package com.alexz.firerecadapter.firestore

import com.alexz.firerecadapter.IEntity
import com.alexz.firerecadapter.viewholder.IBaseViewHolder
import com.google.firebase.firestore.CollectionReference

/**
 * Recycler adapter interface for Firebase Firestore objects accessed as collection
 *
 * @param Entity object class implements [IEntity]
 * @param VH ViewHolder implements [IBaseViewHolder]
 *
 * @see IEntity
 * @see IBaseViewHolder
 */
interface IFirestoreListRecyclerAdapter<Entity : IEntity, VH : IBaseViewHolder<Entity>>
    :IFirestoreRecyclerAdapter<Entity,VH>{

    val entityCollectionReference : CollectionReference
}