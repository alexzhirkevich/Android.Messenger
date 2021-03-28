package com.alexz.messenger.app.ui.common.firerecyclerview

interface AdapterCallback<Model> {
    fun onItemAdded(item: Model)
    fun onItemRemoved(item: Model)
    fun onItemChanged(item: Model) {}
}