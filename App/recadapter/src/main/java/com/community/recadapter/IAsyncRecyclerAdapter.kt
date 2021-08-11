package com.community.recadapter

import android.view.ViewGroup
import com.community.messenger.common.entities.interfaces.IEntity


interface IAsyncRecyclerAdapter<Entity : IEntity, PH : IPlaceHolder<Entity>> {

    var itemLongClickListener: ItemLongClickListener<PH>
    var itemClickListener: ItemClickListener<PH>
    var adapterCallback: AdapterCallback<Entity>
    var onSelectedStateChangedListener : OnSelectedStateChangedListener

    fun onCreatePlaceHolder(parent: ViewGroup, viewType: Int): PH

    fun setVisible(predicate: (Entity) -> Boolean) : Int

    fun setSelected(id : String, selected : Boolean)

    fun isSelected(id : String) : Boolean

    fun add(entity : Entity) : Int

    suspend fun set(entities: Collection<Entity>)

    fun remove(id: String): Boolean

}