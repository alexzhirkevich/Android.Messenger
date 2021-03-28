package com.alexz.messenger.app.ui.common.firerecyclerview

import androidx.annotation.CallSuper


/**
 * ViewHolder interface used in [IFirebaseRecyclerAdapter]
 *
 * */
interface IFirebaseViewHolder<Model> {

    /**
     * Model binded on ViewHolder, used in [IFirebaseRecyclerAdapter.itemClickListener]
     *
     * */
    var model: Model?

    /**
     * Override this method to bind a view holder
     *
     * Used in
     * @see FirebaseMapRecyclerAdapter.onBindViewHolder
     */
    @CallSuper
    fun bind(model: Model){
        this.model = model
    }
}