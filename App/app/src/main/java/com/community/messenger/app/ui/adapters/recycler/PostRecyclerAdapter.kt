package com.community.messenger.app.ui.adapters.recycler

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.community.messenger.app.R
import com.community.messenger.app.ui.views.loadBitmap
import com.community.messenger.common.entities.interfaces.IChannel
import com.community.messenger.common.entities.interfaces.IPost
import com.community.messenger.common.util.MetrixUtil
import com.community.messenger.common.util.dateTime
import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.item_stickyheader.view.*
import ru.surfstudio.android.easyadapter.EasyAdapter
import ru.surfstudio.android.easyadapter.ItemList
import ru.surfstudio.android.easyadapter.controller.BindableItemController
import ru.surfstudio.android.easyadapter.holder.BindableViewHolder
import ru.surfstudio.android.easyadapter.holder.async.AsyncBindableViewHolder
import ru.surfstudio.android.easyadapter.item.BindableItem
import ru.surfstudio.android.recycler.decorator.Decorator
import ru.surfstudio.android.recycler.decorator.MasterDecorator
import ru.surfstudio.android.recycler.extension.sticky.layoutmanager.StickyHeaderHandler
import ru.surfstudio.android.recycler.extension.sticky.layoutmanager.StickyLayoutManager
import java.util.*


class PostRecyclerAdapter(private val recyclerView: RecyclerView, private var channel : IChannel) : EasyAdapter(),
    Observer<IChannel> {

    private companion object {
        private const val VIEW_TYPE_POST = 0
        private const val VIEW_TYPE_DATE = 1
    }

    private val postController = PostItemController()
    private val dateController = DateItemController()

    private val stickyHeaderDecor: StickyHeaderManager<Long, DateViewHolder> by lazy {
        StickyHeaderManager(
            unusedViewHolder = DateViewHolder(
                LayoutInflater.from(recyclerView.context)
                    .inflate(R.layout.item_stickyheader, recyclerView, false)
            ),
            headerMargin = MetrixUtil.dpToPx(recyclerView.context, 5).toFloat(),
            headerHeight = MetrixUtil.dpToPx(recyclerView.context, 5).toFloat() +
                    recyclerView.resources.getDimension(R.dimen.font_size_medium) +
                    MetrixUtil.dpToPx(recyclerView.context, 4),
            gravity = Gravity.CENTER,
            data = { times }
        )
    }

    private val decor: MasterDecorator by lazy {
        Decorator.Builder()
            .overlay(stickyHeaderDecor)
            //.overlay(VIEW_TYPE_POST to PostsDividerDecor())
            .build()
    }
    private val views = mutableMapOf<String, Long>()
    private val shares = mutableMapOf<String, Long>()

    private val times = mutableMapOf<Int, Long>()

    init {
        setAsyncDiffCalculationEnabled(true)
    }

    fun set(collection: List<IPost>) {

        val sorted = collection.sorted()

        val items = ItemList.create(sorted.map { BindableItem(it, postController) })

        var insertions = 0
        times.clear()

        sorted.forEachIndexed { index, post ->

            if (index == 0) {
                items.insert(0 + insertions, post.time, dateController)
                times[0 + insertions] = post.time
                insertions++
            } else {

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = post.time
                }

                val postDay = calendar.get(Calendar.DAY_OF_YEAR)
                val postYear = calendar.get(Calendar.YEAR)

                calendar.timeInMillis = collection[index - 1].time

                val previousPostDay = calendar.get(Calendar.DAY_OF_YEAR)
                val previousPostYear = calendar.get(Calendar.YEAR)


                if (postDay != previousPostDay || postYear != previousPostYear) {
                    items.insert(index + insertions, post.time, dateController)
                    times[index + insertions] = post.time
                    insertions++
                }
            }
        }
        this.items = items
    }

    override fun onChanged(channel: IChannel) {
        this.channel = channel
        notifyItemRangeChanged(0, itemCount)
    }

    fun onPostViewsChanged(postId: String, viewsCount: Long) {
        views[postId] = viewsCount
    }

    fun onPostSharedChanged(postId: String, sharesCount: Long) {
        shares[postId] = sharesCount
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.layoutManager = StickyLayoutManager(
            recyclerView.context,
            object : StickyHeaderHandler {
                override fun getAdapterData(): List<*> {
                    return items
                }
            },
            false
        )
        recyclerView.addItemDecoration(decor)

    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerView.removeItemDecoration(decor)
    }


    private inner class PostItemController : BindableItemController<IPost, PostViewHolder>() {

        override fun viewType(): Int =
            VIEW_TYPE_POST


        override fun getItemId(data: IPost): Pair<String, Long> =
            data.id to data.time

        override fun createViewHolder(parent: ViewGroup): PostViewHolder =
            PostViewHolder(parent)

    }

    private inner class DateItemController : BindableItemController<Long, DateViewHolder>() {

        override fun viewType(): Int =
            VIEW_TYPE_DATE

        override fun createViewHolder(parent: ViewGroup): DateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stickyheader, parent, false)
            return DateViewHolder(view)
        }

        override fun getItemId(data: Long): Any = data

    }

    private inner class PostViewHolder(parent: ViewGroup) : AsyncBindableViewHolder<IPost>(
        parent,
        R.layout.item_post,
        containerWidth = MATCH_PARENT
    ) {

        override fun bindInternal(data: IPost?) {

            val time = data?.time?.dateTime(itemView.context)

            itemView.post_name.text = channel.name
            itemView.post_avatar.apply {
                if (channel.imageUri.isEmpty())
                    setupWithText(channel.name, R.dimen.font_size_medium, R.color.channels)
                else
                    setImageURI(Uri.parse(channel.imageUri))
            }

            data?.let {
                itemView.post_text.text = it.text
                itemView.post_date.text = time!!.time
            } ?: run {
                itemView.post_text.text = ""
                itemView.post_date.text = ""
            }
        }
    }

    interface StickyHolder

    private class DateViewHolder(view: View) : BindableViewHolder<Long>(view), StickyHolder {

        var time = Long.MAX_VALUE

        override fun bind(data: Long?) {
            data?.let {
                time = data
                itemView.header_text.text = it.dateTime(itemView.context).dayAndMonthLocalized
            }
        }

    }

//    private class PostsDividerDecor : Decorator.ViewHolderDecor {
//
//        private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        private val alpha = dividerPaint.alpha
//        private var scrollbarSize: Int = 0
//
//        private val paintInit = SingleAction()
//
//        override fun draw(
//            canvas: Canvas,
//            view: View,
//            recyclerView: RecyclerView,
//            state: RecyclerView.State
//        ) {
//
//            paintInit.doSingle {
//                dividerPaint.color = Color.BLACK
//                dividerPaint.strokeWidth =
//                    view.resources.getDimension(R.dimen.post_padding_vertical)
//                scrollbarSize = view.resources.getDimensionPixelSize(R.dimen.scrollbar_width)
//            }
//
//            val viewHolder = recyclerView.getChildViewHolder(view)
//            val nextViewHolder =
//                recyclerView.findViewHolderForAdapterPosition(viewHolder.adapterPosition + 1)
//
//            val startX = recyclerView.paddingLeft
//            val startY = view.bottom + view.translationY
//            val stopX = recyclerView.width - recyclerView.paddingRight
//            val stopY = startY
//
//            dividerPaint.alpha = (view.alpha * alpha).toInt()
//
//            canvas.drawLine(startX.toFloat(), startY, stopX.toFloat(), stopY, dividerPaint)
//        }
//    }

//    private class DateDecor : Decorator.ViewHolderDecor {
//
//        private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//        private val alpha = dividerPaint.alpha
//        private var scrollbarSize: Int = 0
//
//        private val paintInit = SingleAction()
//
//        override fun draw(
//            canvas: Canvas,
//            view: View,
//            recyclerView: RecyclerView,
//            state: RecyclerView.State
//        ) {
//
//            paintInit.doSingle {
//                dividerPaint.color = Color.BLACK
//                dividerPaint.strokeWidth =
//                    view.resources.getDimension(R.dimen.post_padding_vertical)
//                scrollbarSize = view.resources.getDimensionPixelSize(R.dimen.scrollbar_width)
//            }
//
//            val viewHolder = recyclerView.getChildViewHolder(view)
//            val nextViewHolder =
//                recyclerView.findViewHolderForAdapterPosition(viewHolder.adapterPosition + 1)
//
//            val startX = recyclerView.paddingLeft
//            val startY = view.bottom + view.translationY
//            val stopX = recyclerView.width - recyclerView.paddingRight
//            val stopY = startY
//
//            dividerPaint.alpha = (view.alpha * alpha).toInt()
//
//            canvas.drawLine(startX.toFloat(), startY, stopX.toFloat(), stopY, dividerPaint)
//        }
//    }


}

class StickyHeaderManager<T,VH : BindableViewHolder<T>>(
    private val unusedViewHolder: VH,
    private val headerMargin : Float,
    private val headerHeight : Float,
    private val gravity : Int,
    private val data : () -> Map<Int,T>,
    ) : Decorator.RecyclerViewDecor {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentHeaderBitmap: Bitmap? = null
    private val screenWidth = unusedViewHolder.itemView.resources.displayMetrics.widthPixels.toFloat()

    override fun draw(canvas: Canvas, recyclerView: RecyclerView, state: RecyclerView.State) {
        val firstVisibleIndex = (recyclerView.layoutManager as LinearLayoutManager)
            .findFirstVisibleItemPosition()

        val data = data()

        val previousDateIndex = data.filterKeys {
            it <= firstVisibleIndex
        }.keys.lastOrNull() ?: 0

        val dataToDisplay = data[previousDateIndex]

        val stickyHolders = recyclerView.children
            .map { recyclerView.findContainingViewHolder(it) }
            .filterIndexed { index, viewHolder ->
                viewHolder?.javaClass == unusedViewHolder.javaClass
            }.toList()


        val firstStickyHolder = stickyHolders.firstOrNull() as? VH
        unusedViewHolder.bind(dataToDisplay)
        val bitmap: Bitmap = unusedViewHolder.itemView.loadBitmap(height = headerHeight.toInt())


        stickyHolders.forEach { it?.itemView?.alpha = 1f }
        currentHeaderBitmap = bitmap

        val bitmapStartOffset =when(gravity){
            Gravity.END -> screenWidth-bitmap.width
            Gravity.CENTER  -> (screenWidth-bitmap.width)/2
            Gravity.CENTER_HORIZONTAL -> (screenWidth-bitmap.width)/2
            else -> 0f
        }
        if (firstStickyHolder == null){
            canvas.drawBitmap(bitmap,bitmapStartOffset,headerMargin,paint)
            return
        }

        val viewHolderY =  firstStickyHolder.itemView.y


        //calculate bitmap top offset
        val bitmapHeight = currentHeaderBitmap?.height ?: 0
        val bitmapTopOffset = if (viewHolderY >= 0 && viewHolderY <= bitmapHeight + headerMargin) {
            viewHolderY - bitmapHeight
        } else {
            headerMargin
        }

        firstStickyHolder.itemView.alpha = if (viewHolderY < 0f)
            0f
        else {
            1f
        }

        //draw bitmap header
        currentHeaderBitmap?.let {
            canvas.drawBitmap(it, bitmapStartOffset, bitmapTopOffset, paint)
        }
    }


}


