package com.alexz.firerecadapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<Entity>(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        IBaseViewHolder<Entity> {

    override var entity: Entity? = null
}