package com.alexz.firerecadapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class FirebaseViewHolder<Entity>(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        IFirebaseViewHolder<Entity> {

    override var entity: Entity? = null
}