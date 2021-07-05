package com.alexz.firerecadapter

interface AdapterCallback<Entity> {
    fun onItemAdded(item: Entity) {}
    fun onItemRemoved(item: Entity) {}
    fun onItemChanged(item: Entity) {}
}