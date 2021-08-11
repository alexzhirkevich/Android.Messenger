package com.community.recadapter

import android.view.View
import com.community.messenger.common.entities.interfaces.IEntity

interface IPlaceHolder<Entity : IEntity> : IBaseViewHolder<Entity> {

    val isInflated : Boolean

    val itemView : View

    fun onFinishInflate(view : View)
}