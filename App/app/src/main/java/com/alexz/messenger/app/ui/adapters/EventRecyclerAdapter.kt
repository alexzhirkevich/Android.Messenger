package com.alexz.messenger.app.ui.adapters

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.alexz.firerecadapter.BaseRecyclerAdapter
import com.alexz.firerecadapter.ItemClickListener
import com.alexz.firerecadapter.viewholder.BaseViewHolder
import com.alexz.messenger.app.data.entities.interfaces.IEvent
import com.alexz.messenger.app.data.providers.imp.DaggerUsersProviderComponent
import com.alexz.messenger.app.data.providers.interfaces.UsersProvider
import com.alexz.messenger.app.ui.views.setTopMargin
import com.alexz.messenger.app.util.timeVisualizer
import com.messenger.app.R

class EventRecyclerAdapter : BaseRecyclerAdapter<IEvent, EventRecyclerAdapter.EventViewHolder>(){

    private val favourites = mutableMapOf<String,Boolean>()
    private val visibleDescription = mutableMapOf<String,Boolean>()
    private val isEnded = mutableMapOf<String,Boolean>()

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_constraint,parent,false)
        return EventViewHolder(view)
    }

    var starClickListener : ItemClickListener<EventViewHolder>? = null
    var endButtonClickListener : ItemClickListener<EventViewHolder>? = null
    var deleteButtonClickListener : ItemClickListener<EventViewHolder>? = null
    var shareClickListener : ItemClickListener<EventViewHolder>? = null
    var editClickListener : ItemClickListener<EventViewHolder>? = null
    var eventClickListener : ItemClickListener<EventViewHolder>? = null

    inner class EventViewHolder(view: View) : BaseViewHolder<IEvent>(view) {

        private val tvName = view.findViewById<TextView>(R.id.event_name)
        private val tvDate = view.findViewById<TextView>(R.id.event_date)
        private val tvTime = view.findViewById<TextView>(R.id.event_time)
        private val tvAddress = view.findViewById<TextView>(R.id.event_address)
        private val tvDescription = view.findViewById<TextView>(R.id.event_description)
        private val tvEnded = view.findViewById<TextView>(R.id.event_ended)
        private val layoutDescription = view.findViewById<ViewGroup>(R.id.event_layout_description)
        private val layoutInfo = view.findViewById<ViewGroup>(R.id.event_layout_info).apply {
            setOnClickListener {
                eventClickListener?.invoke(this@EventViewHolder)
            }
        }
        private val btnEdit = view.findViewById<Button>(R.id.event_btn_edit).apply {
            setOnClickListener {
                editClickListener?.invoke(this@EventViewHolder)
            }
        }
        private val btnShare = view.findViewById<Button>(R.id.event_btn_share).apply {
            setOnClickListener {
                shareClickListener?.invoke(this@EventViewHolder)
            }
        }

        private val btnEnd = view.findViewById<ImageButton>(R.id.event_btn_end).apply {
            setOnClickListener {
                endButtonClickListener?.invoke(this@EventViewHolder)
            }
        }

        private val btnDelete = view.findViewById<ImageButton>(R.id.event_btn_delete).apply {
            setOnClickListener {
                deleteButtonClickListener?.invoke(this@EventViewHolder)
            }
        }

        private val btnStar = view.findViewById<ImageView>(R.id.event_favourite).apply {
            setOnClickListener {
                starClickListener?.invoke(this@EventViewHolder)
            }
        }

        var isFavourite: Boolean
            get() = favourites[entity?.id] ?: false
            set(value) {
                entity?.id?.let { favourites[it] = value }
                btnStar.setImageResource(if (value) R.drawable.ic_star else R.drawable.ic_star_outline)
            }

        override fun bind(entity: IEvent) {
            super.bind(entity)
            isFavourite = favourites[entity.id] == true

            val isOwner =  usersProvider.currentUserId == entity.creatorId

            tvEnded.isVisible  = entity.isEnded || !entity.isValid
            layoutDescription.isVisible = !entity.isEnded && entity.isValid
            btnStar.isVisible = !entity.isEnded && entity.isValid

            btnDelete.apply {
                val deleteOnly = (entity.isEnded || !entity.isValid || !isOwner)
                setBackgroundResource(if (deleteOnly) R.drawable.btn_event_delete_big else R.drawable.btn_event_delete)
                (layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight =
                        if (deleteOnly) 1f else 0.5f

                requestLayout()
                btnEnd.isVisible = !deleteOnly
            }


            layoutInfo.apply {
                isClickable = !entity.isEnded && entity.isValid
                isFocusable = isClickable
                setBackgroundResource(if (isClickable) R.drawable.background_event_ripple else R.drawable.background_event)
            }

            btnEdit.apply {
                isEnabled = isOwner && !entity.isEnded
                setTextColor(ContextCompat.getColor(context,if (isEnabled) R.color.white else R.color.gray))
            }


            tvEnded.apply {
                isVisible = (entity.isEnded || !entity.isValid)
                if (entity.isEnded)
                    setText(R.string.finished)
                if (!entity.isValid)
                    setText(R.string.cancelled)
            }

            btnStar.isVisible = entity.isValid && !entity.isEnded

            tvName.text = entity.name
            tvDate.text = entity.time.timeVisualizer().dateNoYear
            tvTime.text = entity.time.timeVisualizer().time
            tvAddress.text = entity.address
            tvDescription.text = entity.description

            setDescriptionVisibleImmediate(visibleDescription[entity.id] == true)
        }

        private var animRunning = false


        private fun setDescriptionVisibleImmediate(visible: Boolean){
            entity?.id?.let { visibleDescription[it] = visible }
            layoutDescription.setTopMargin(
                    if (visible)
                        itemView.resources.getDimension(R.dimen.event_description_base_top_margin).toInt()
                    else
                        -Int.MAX_VALUE
            )
        }

        fun setDescriptionVisible(visible : Boolean) : Boolean {
            if (!animRunning) {
                entity?.id?.let { visibleDescription[it] = visible }
                animRunning = true
                val borders = intArrayOf(
                        itemView.resources.getDimension(R.dimen.event_description_base_top_margin).toInt(),
                        -layoutDescription.height)

                val valueAnimator = if (!visible)
                    ValueAnimator.ofInt(borders[0], borders[1])
                else
                    ValueAnimator.ofInt(borders[1], borders[0])

                valueAnimator.apply {
                    addUpdateListener {
                        layoutDescription.setTopMargin(it.animatedValue as Int)
                    }
                    doOnEnd {
                        animRunning = false
                    }
                    duration = itemView.resources.getInteger(R.integer.anim_duration_medium).toLong()
                    interpolator = DecelerateInterpolator()
                }.start()
                return true
            } else
                return false
        }

        val isDescriptionVisible: Boolean
            get() = (layoutDescription.layoutParams as ViewGroup.MarginLayoutParams).topMargin >
                    itemView.resources.getDimension(R.dimen.event_description_base_top_margin) - 1
    }

    private companion object{
        private val usersProvider : UsersProvider by lazy {
            DaggerUsersProviderComponent.create().getUsersProvider()
        }
    }
}