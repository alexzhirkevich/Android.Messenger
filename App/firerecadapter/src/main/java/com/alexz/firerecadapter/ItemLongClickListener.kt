package com.alexz.firerecadapter

typealias ItemLongClickListener<VH> = (VH) -> Boolean


//interface ItemClickListener<ViewHolder : IBaseViewHolder<out IEntity>> {
//    @UiThread
//    fun onItemClick(viewHolder: ViewHolder) {}
//}

typealias ItemClickListener<VH> = (VH) -> Unit

