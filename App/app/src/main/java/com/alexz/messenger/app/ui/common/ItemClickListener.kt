package com.alexz.messenger.app.ui.common

import android.view.View

interface ItemClickListener<T> {
    fun onItemClick(view: View, data: T?) {}
    fun onLongItemClick(view: View, data: T?): Boolean {
        return false
    }
}