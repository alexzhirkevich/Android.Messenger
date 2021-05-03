package com.alexz.firerecadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexz.firerecadapter.viewholder.IFirebaseViewHolder

/**
 * Recycler adapter interface for Firebase Realtime Database objects.
 * Basic interface, provides offline access.
 *
 * @param Entity object class implements [IEntity]
 * @param VH ViewHolder implements [IFirebaseViewHolder]
 *
 * @see IEntity
 * @see IFirebaseViewHolder
 */
interface IBaseRecyclerAdapter<Entity : IEntity, VH : IFirebaseViewHolder<Entity>> {

    /**
     * Used in [IBaseRecyclerAdapter.parse] to parse models by default
     * */
    val modelClass : Class<Entity>

    var itemClickListener: ItemClickListener<Entity>?
    var adapterCallback: AdapterCallback<Entity>?
    var loadingCallback: LoadingCallback?

    var localProvider: ILocalProvider<Entity>?

    /**
     * Same as [RecyclerView.Adapter.onCreateViewHolder]
     *
     * Used in [RealtimeDatabaseListRecyclerAdapter.onCreateViewHolder]
     *
     * @return [FirebaseViewHolder]
     * @see IFirebaseViewHolder
     */
    fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * Decide if model must be selected by selection [key].
     * Returns true by default (shows all views with any keys)
     *
     * Used in [IBaseRecyclerAdapter.select]
     * @see IBaseRecyclerAdapter.select
     * *
     * @return Entity field for selection
     */
    fun onSelect(key: String, model: Entity): Boolean = true

    /**
     * Shows only ViewHolders accepted by [IBaseRecyclerAdapter.onSelect]
     *
     * Override [IBaseRecyclerAdapter.onSelect] to be able to select visible items.
     *
     * @param key key for selection. Shows all items, if param is null
     * @return selected count
     */
    fun select(key: String?): Int

    /**
     * Used for [model] adding. Can be changed after approving in database
     *
     * @return inserted idx or changed idx, if model with that [IEntity.id] is already exists
     * */
    fun add(model : Entity, forceCallback: Boolean = false, byUser : Boolean = true) : Int

    /**
     * Used for [model] inserting to position by [idx]. Can be changed after approving in database
     *
     * @return inserted idx or changed idx, if model with that [IEntity.id] is already exists
     * */
    fun insert(idx: Int, model: Entity, forceCallback: Boolean = false, byUser : Boolean = true) : Int

    /**
     * Used for models removing. Models can be returned, if adapter is listening for database changes
     * and model with this [id] is still in DB
     * */
    fun remove(id: String, byUser : Boolean = true): Boolean

    /**
     * Shows all ViewHolders
     */
    fun selectAll()
}