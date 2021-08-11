package com.community.recadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<Entity>(itemView: View) :
        RecyclerView.ViewHolder(itemView),
    IBaseViewHolder<Entity> {

    override var entity: Entity? = null

    override var position: Int? =0
}