package com.community.messenger.app.ui.adapters.recycler

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.community.messenger.app.R
import com.community.messenger.app.ui.views.setTopMargin
import com.community.messenger.common.entities.interfaces.IEvent
import com.community.messenger.common.util.dateTime
import com.community.messenger.core.providers.components.DaggerUsersProviderComponent
import com.community.messenger.core.providers.interfaces.UsersProvider
import com.community.recadapter.ItemClickListener
import com.community.recadapter.ItemLongClickListener
import com.community.recadapter.BaseViewHolder


class EventRecyclerAdapter : com.community.recadapter.BaseRecyclerAdapter<IEvent, EventRecyclerAdapter.EventViewHolder>(){

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
    var eventLongClickListener : ItemLongClickListener<EventViewHolder>? = null
    var dateClickListener : ItemClickListener<EventViewHolder>? = null
    var timeClickListener : ItemClickListener<EventViewHolder>? = null
    var addressClickListener : ItemClickListener<EventViewHolder>? = null

    inner class EventViewHolder(view: View) : BaseViewHolder<IEvent>(view) {

        var isFavourite: Boolean
            get() = favourites[entity?.id] ?: false
            set(value) {
                entity?.id?.let { favourites[it] = value }
                btnStar.setImageResource(if (value) R.drawable.ic_star else R.drawable.ic_star_outline)
            }

        private val tvBackgroundAlpha = 180

        private val descriptionAnimDuration : Long by lazy {
            itemView.resources.getInteger(R.integer.anim_duration_medium).toLong()
        }


        private val tvName = view.findViewById<TextView>(R.id.event_name).apply {
           background.alpha = tvBackgroundAlpha
        }

        private val ivBackground = view.findViewById<ImageView>(R.id.event_background)
        private val tvDate = view.findViewById<TextView>(R.id.event_date).apply {
            setOnClickListener {
                dateClickListener?.invoke(this@EventViewHolder)
            }
            background.alpha = tvBackgroundAlpha
        }
        private val tvTime = view.findViewById<TextView>(R.id.event_time).apply {
            setOnClickListener {
                timeClickListener?.invoke(this@EventViewHolder)
            }
            background.alpha = tvBackgroundAlpha
        }
        private val tvAddress = view.findViewById<TextView>(R.id.event_address).apply {
            setOnClickListener {
                addressClickListener?.invoke(this@EventViewHolder)
            }
            background.alpha = tvBackgroundAlpha
        }
        private val tvDescription = view.findViewById<TextView>(R.id.event_description)

        @SuppressLint("ClickableViewAccessibility")
        private val tvEnded = view.findViewById<TextView>(R.id.event_ended).apply {
            setOnTouchListener { _, _ ->  true}
        }
        private val layoutDescription = view.findViewById<ViewGroup>(R.id.event_layout_description)
        private val layoutInfo = view.findViewById<ViewGroup>(R.id.event_layout_info).apply {
            setOnClickListener {
                eventClickListener?.invoke(this@EventViewHolder)
            }
            setOnLongClickListener {
                eventLongClickListener?.invoke(this@EventViewHolder) ?: false
            }
        }
        private val btnEdit = view.findViewById<TextView>(R.id.event_btn_edit).apply {
            setOnClickListener {
                editClickListener?.invoke(this@EventViewHolder)
            }
        }
        private val btnShare = view.findViewById<TextView>(R.id.event_btn_share).apply {
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
            background.alpha = tvBackgroundAlpha
        }


        fun removeAnimated() {
            val id = entity?.id
            itemView.postDelayed({
                val height = itemView.height
                val margin = itemView.resources.getDimension(R.dimen.event_vertical_margin).toInt()

                ValueAnimator.ofInt(layoutInfo.height,0).apply {
                    addUpdateListener {
                        itemView.apply {
                            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                                val value = animatedValue as Int
                                val animProgress = value.toFloat()/height
                                this.height = value
                                this.topMargin = (margin* animProgress).toInt()
                                this.bottomMargin = topMargin
                            }

                            requestLayout()
                        }
                    }
                    doOnEnd {
                        if (id != null) remove(id)
                    }
                }.start()
            },if (isDescriptionVisible) descriptionAnimDuration else 0)

            if (isDescriptionVisible)
                setDescriptionVisible(false)
        }

        override fun onBind(entity: IEvent) {

            itemView.apply {
                (layoutParams as ViewGroup.MarginLayoutParams).apply {
                    this.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    this.topMargin = itemView.resources.getDimension(R.dimen.event_vertical_margin).toInt()
                    this.bottomMargin = topMargin
                }
                requestLayout()
            }

            isFavourite = favourites[entity.id] == true

            val isOwner =  usersProvider.currentUserId == entity.creatorId

            tvEnded.isVisible  = entity.isEnded || !entity.isValid
            layoutDescription.isVisible = !entity.isEnded && entity.isValid
            btnStar.isVisible = !entity.isEnded && entity.isValid

            btnDelete.apply {
                val deleteOnly = (entity.isEnded || !entity.isValid || !isOwner)
                (layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight =
                        if (deleteOnly) 1f else 0.5f

                requestLayout()
                btnEnd.isVisible = !deleteOnly
            }

            ivBackground.apply {
                if (entity.imageUri.isNotEmpty()) {
                    Glide.with(this).load(entity.imageUri)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(this)
                }
                isVisible = entity.imageUri.isNotEmpty()
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

            val tv = entity.time.dateTime(itemView.context)

            tvName.text = entity.name
            tvDate.text = tv.dateNoYear
            tvTime.text = tv.time
            tvAddress.text = entity.address
            tvDescription.text = entity.description

            setDescriptionVisibleImmediate(visibleDescription[entity.id] == true)
        }

        fun setDescriptionVisible(visible : Boolean) : Boolean {
            if (!animRunning) {
                entity?.id?.let { visibleDescription[it] = visible }

                animRunning = true
                val borders = intArrayOf(
                        itemView.resources.getDimension(R.dimen.event_description_base_top_margin).toInt(),
                        -(layoutDescription.height + itemView.resources.getDimension(R.dimen.event_cardview_margin)+10).toInt())

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
                    duration = descriptionAnimDuration
                    interpolator = DecelerateInterpolator(0.8f)
                }.start()
                return true
            } else
                return false
        }

        val isDescriptionVisible: Boolean
            get() = (layoutDescription.layoutParams as ViewGroup.MarginLayoutParams).topMargin >
                    itemView.resources.getDimension(R.dimen.event_description_base_top_margin) - 1

        private var animRunning = false

        private fun setDescriptionVisibleImmediate(visible: Boolean) {

            val margin = if (visible)
                itemView.resources.getDimension(R.dimen.event_description_base_top_margin).toInt()
            else
                -Int.MAX_VALUE

            layoutDescription.setTopMargin(margin)
        }
    }



    private companion object{
        private val usersProvider : UsersProvider by lazy {
            DaggerUsersProviderComponent.create().getProvider()
        }
    }
}