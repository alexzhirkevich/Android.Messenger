package com.alexz.firerecadapter

import androidx.annotation.UiThread
import com.alexz.firerecadapter.viewholder.FirebaseViewHolder

interface ItemClickListener<T> {

    @UiThread
    fun onItemClick(viewHolder: FirebaseViewHolder<T>) {}

    @UiThread
    fun onLongItemClick(viewHolder: FirebaseViewHolder<T>): Boolean {
        return false
    }
}