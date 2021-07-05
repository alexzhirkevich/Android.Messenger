package com.alexz.firerecadapter.viewholder

import androidx.annotation.CallSuper


/**
 * ViewHolder interface used in [IBaseRecyclerAdapter]
 *
 * */
interface IBaseViewHolder<Entity> {

    /**
     * Entity, binded on ViewHolder. Used in [IBaseRecyclerAdapter.itemClickListener]
     * */
    var entity: Entity?

    /**
     * Override this method to bind a view holder. Super method must be called
     *
     * Used in
     * @see RealtimeDatabaseListRecyclerAdapter.onBindViewHolder
     */
    @CallSuper
    fun bind(entity: Entity){
        this.entity = entity
    }
}