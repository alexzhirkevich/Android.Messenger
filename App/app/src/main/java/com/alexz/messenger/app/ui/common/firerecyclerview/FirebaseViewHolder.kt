package com.alexz.messenger.app.ui.common.firerecyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class FirebaseViewHolder<Model>(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        IFirebaseViewHolder<Model> {

    override var model: Model? = null
}