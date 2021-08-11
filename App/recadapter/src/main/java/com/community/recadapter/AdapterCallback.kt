package com.community.recadapter

interface AdapterCallback<Entity> {
    fun onItemAdded(item: Entity) {}
    fun onItemRemoved(item: Entity) {}
    fun onItemChanged(item: Entity) {}
}