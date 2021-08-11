package com.community.recadapter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import com.community.messenger.common.entities.interfaces.IEntity
import kotlinx.coroutines.CoroutineScope

abstract class PlaceHolder<Entity : IEntity>(parent : ViewGroup, @LayoutRes layoutId : Int, scope : CoroutineScope)
    : FrameLayout(parent.context), IPlaceHolder<Entity> {

    final override var entity: Entity? =null

    final override var position: Int? = -1

    final override var isInflated = false
        private set

    final override lateinit var itemView: View
        private set

    private val binds : MutableList<Entity> = mutableListOf()

    internal fun bind(entity: Entity){
        if (isInflated){
            onBind(entity)
        } else
            binds.add(entity)
    }

    init {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            AsyncLayoutInflater(parent.context).inflate(layoutId, parent) { view, _, _ ->
                addView(view)
                itemView = view
                isInflated = true
                onFinishInflate(view)
                binds.forEach { onBind(it) }
                binds.clear()
            }
    }

    override fun onFinishInflate(view: View) {

    }
}

