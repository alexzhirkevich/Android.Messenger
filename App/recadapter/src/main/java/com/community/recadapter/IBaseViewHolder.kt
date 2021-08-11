package com.community.recadapter


/**
 * ViewHolder interface used in [IBaseRecyclerAdapter]
 *
 * */
interface IBaseViewHolder<Entity> {

    var entity: Entity?

    var position : Int?

    /**
     * Override this method to bind a view holder
     *
     * Used in
     * @see RealtimeDatabaseListRecyclerAdapter.onBindViewHolder
     */
    fun onBind(entity: Entity)
}