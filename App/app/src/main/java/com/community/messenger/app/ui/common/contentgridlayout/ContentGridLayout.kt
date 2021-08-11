//package com.community.messenger.app.ui.common.contentgridlayout
//
//import android.app.Activity
//import android.content.Context
//import android.net.Uri
//import android.util.AttributeSet
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import androidx.appcompat.content.res.AppCompatResources
//import androidx.core.app.ActivityOptionsCompat
//import androidx.gridlayout.widget.GridLayout
//import com.community.messenger.common.entities.interfaces.IMediaContent
//import com.community.messenger.app.ui.activities.FullscreenImageActivity
//import com.community.messenger.app.ui.activities.FullscreenVideoActivity
//import com.community.messenger.common.util.MetrixUtil
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import com.google.android.exoplayer2.C
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
//import com.google.android.exoplayer2.ui.PlayerView
//import com.messenger.app.BuildConfig
//import com.community.messenger.app.R
//
//class ContentGridLayout : GridLayout {
//
//    private val contents = HashMap<IMediaContent, View>()
//    private var paddings = 0
//
//    private var recycledViews = HashMap<Int, MutableSet<View>>()
//
//    val content: List<IMediaContent>
//        get() = contents.keys.toList()
//
//    var contentClickListener: ContentClickListener? = null
//
//    constructor(context: Context) : super(context)
//
//    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
//
//    fun clearContent() {
//        //contents.forEach { recycle(it.key.type, it.value) }
//        contents.clear()
//    }
//
//    fun addContent(content: IMediaContent) {
//        if (contents.size >= 10) {
//            //val last = this.content.last()
//            //recycle(last.type, contents[last])
//            contents.remove(this.content.last())
//        }
//        contents[content] = createViewForContent(content)
//    }
//
//    fun reGroup() {
//        try {
//            removeAllViewsInLayout()
//            when (contents.size) {
//                1 -> {
//                    rowCount = 1
//                    columnCount = 1
//                    val view = contents[content[0]]
//                    (view?.layoutParams as LayoutParams?)?.apply {
//                        columnSpec = spec(UNDEFINED, 1f)
//                        rowSpec = spec(UNDEFINED, 1f)
//                    }
//                    view?.requestLayout()
//                    addView(view)
//                }
//                2 -> {
//                    rowCount = 1
//                    columnCount = 2
//                    for (i in 0..1) {
//                        val view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            columnSpec = spec(UNDEFINED, 1f)
//                            rowSpec = spec(UNDEFINED, 1f)
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                3, 4 -> {
//                    columnCount = 2
//                    rowCount = contents.size - 1
//                    var view = contents[content[0]]
//                    (view?.layoutParams as LayoutParams?)?.apply {
//                        columnSpec = spec(UNDEFINED, 1, contents.size - 1.toFloat())
//                        rowSpec = spec(UNDEFINED, contents.size - 1, contents.size - 1.toFloat())
//                    }
//                    view?.requestLayout()
//                    addView(view)
//
//                    for (i in 1 until contents.size) {
//                        view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            columnSpec = spec(UNDEFINED, 1, 1f)
//                            rowSpec = spec(UNDEFINED, 1, 1f)
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                5 -> {
//                    columnCount = 3
//                    rowCount = 3
//                    for (i in 0..1) {
//                        val view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            columnSpec = spec(UNDEFINED, 2 - i, 1f)
//                            rowSpec = spec(UNDEFINED, 2, 2f)
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                    for (i in 2 until 5) {
//                        val view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            columnSpec = spec(UNDEFINED, 1, 1f)
//                            rowSpec = spec(UNDEFINED, 1, 1f)
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                6 -> {
//                    columnCount = 3
//                    rowCount = 3
//                    var view = contents[content[0]]
//                    (view?.layoutParams as LayoutParams?)?.apply {
//                        columnSpec = spec(UNDEFINED, 2, 1f)
//                        rowSpec = spec(UNDEFINED, 2, 2f)
//                    }
//                    view?.requestLayout()
//                    addView(view)
//                    for (i in 1 until 6) {
//                        view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            columnSpec = spec(UNDEFINED, 1, 1f)
//                            rowSpec = spec(UNDEFINED, 1, 1f)
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                7 -> {
//                    columnCount = 3
//                    rowCount = 4
//                    var view = contents[content[0]]
//                    (view?.layoutParams as LayoutParams?)?.apply {
//                        columnSpec = spec(UNDEFINED, 3, 1f)
//                        rowSpec = spec(UNDEFINED, 3, 3f)
//                    }
//                    view?.requestLayout()
//                    addView(view)
//
//                    for (i in 1 until 7) {
//                        view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            columnSpec = spec(UNDEFINED, 1, 1f)
//                            rowSpec = spec(UNDEFINED, 1, 1f)
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                8 -> {
//                    columnCount = 2
//                    rowCount = 4
//                    for (i in 0 until 8) {
//                        val view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            columnSpec = spec(UNDEFINED, 1, 1f)
//                            rowSpec = spec(UNDEFINED, 1, 1f)
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                9 -> {
//                    columnCount = 6
//                    rowCount = 4
//                    for (i in 0 until 9) {
//                        val view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            if (i < 6) {
//                                columnSpec = spec(UNDEFINED, 3, 3f)
//                                rowSpec = spec(UNDEFINED, 1, 2f)
//                            } else {
//                                columnSpec = spec(UNDEFINED, 2, 2f)
//                                rowSpec = spec(UNDEFINED, 1, 1f)
//                            }
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                10 -> {
//                    columnCount = 6
//                    rowCount = 4
//                    for (i in 0 until 10) {
//                        val view = contents[content[i]]
//                        (view?.layoutParams as LayoutParams?)?.apply {
//                            if (i < 4) {
//                                columnSpec = spec(UNDEFINED, 3, 3f)
//                                rowSpec = spec(UNDEFINED, 1, 2f)
//                            } else {
//                                columnSpec = spec(UNDEFINED, 2, 2f)
//                                rowSpec = spec(UNDEFINED, 1, 1f)
//                            }
//                        }
//                        view?.requestLayout()
//                        addView(view)
//                    }
//                }
//                else -> if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "Invalid content count: " + contents.size)
//                }
//            }
//            requestLayout()
//            invalidate()
//        } catch (ignore: Exception) {
//        }
//    }
//
//    init {
//        orientation = HORIZONTAL
//        paddings = MetrixUtil.dpToPx(context, 1)
//    }
//
//    fun setFullscreenTransition(transitionActivity: Activity?) {
//        contentClickListener = object : ContentClickListener {
//            override fun onImageClick(view: ImageView, content: IMediaContent) {
//                if (transitionActivity != null) {
//                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            transitionActivity, view, context.getString(R.string.util_transition_image_fullscreen))
//                    FullscreenImageActivity.startActivity(transitionActivity, content.url, options.toBundle())
//                }
//            }
//
//            override fun onVideoClick(playerView: PlayerView, content: IMediaContent) {
//                if (transitionActivity != null) {
//                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            transitionActivity, playerView, context.getString(R.string.util_transition_video_fullscreen))
//                    FullscreenVideoActivity.startActivity(transitionActivity, content.url, options.toBundle())
//                }
//            }
//        }
//    }
//
//    private fun createViewForContent(content: IMediaContent): View {
//        val view = when (content.type) {
//            IMediaContent.IMAGE -> {
//                //val v = getRecycled(IMediaContent.IMAGE)
//                val view = ImageView(context)
//                view.scaleType = ImageView.ScaleType.CENTER_CROP
//                view.transitionName = resources.getString(R.string.util_transition_image_fullscreen)
//
//                Glide.with(this)
//                        .load(content.url)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .centerCrop()
//                        .into(view)
//
//                view.setOnClickListener {
//                    contentClickListener?.onImageClick(view, content)
//                }
//                view
//            }
//            IMediaContent.VIDEO -> {
//                val view = /*getRecycled(IMediaContent.IMAGE) as PlayerView? ?:*/ PlayerView(context)
//                view.useArtwork = true
//                view.defaultArtwork = AppCompatResources.getDrawable(context, R.drawable.logo512)
//                view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
//                view.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
//                view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
//                val player = SimpleExoPlayer.Builder(context).build()
//                view.player = player
//                player.addListener(object : Player.EventListener {
//                    override fun onPlaybackStateChanged(state: Int) {
//                        if (state == Player.STATE_IDLE) {
//                            contentClickListener?.onVideoClick(view, content)
//                            player.stop()
//                        }
//                    }
//                })
//                val mediaItem = MediaItem.fromUri(Uri.parse(content.url))
//                player.setMediaItem(mediaItem)
//                player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
//                view.transitionName = context.getString(R.string.util_transition_video_fullscreen)
//                view.setOnClickListener {
//                    contentClickListener?.onVideoClick(view, content)
//                }
//                view
//            }
//            else -> throw IllegalArgumentException("Invalid MediaContent type: ${content.type}")
//        }
//        view.layoutParams = LayoutParams().apply {
//        }
//        view.setPadding(paddings, paddings, paddings, paddings)
//        view.isClickable = true
//        view.isFocusable = true
//        view.isLongClickable = true
//        return view
//    }
//
////    private fun getRecycled(type: Int) = synchronized(recycledViews) {
////        try {
////            val set = recycledViews[type]
////            if (set != null && set.isNotEmpty()) {
////                val v = recycledViews[type]?.first()
////                recycledViews[type]?.remove(v)
////                v
////            } else null
////        } catch (ignore: Exception) {
////            null
////        }
////    }
//
////    private fun recycle(type:Int,view:View?) = synchronized(recycledViews){
////        if (view != null) {
////            if (recycledViews.containsKey(type)) {
////                recycledViews[type]?.add(view)
////            } else {
////                recycledViews[type] = mutableSetOf(view)
////            }
////        }
////    }
//
//    companion object {
//        private val TAG = ContentGridLayout::class.java.simpleName
//    }
//}