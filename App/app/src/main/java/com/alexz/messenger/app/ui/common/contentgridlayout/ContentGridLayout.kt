package com.alexz.messenger.app.ui.common.contentgridlayout

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityOptionsCompat
import androidx.gridlayout.widget.GridLayout
import com.alexz.messenger.app.data.model.interfaces.IMediaContent
import com.alexz.messenger.app.ui.activities.FullscreenImageActivity
import com.alexz.messenger.app.ui.activities.FullscreenVideoActivity
import com.alexz.messenger.app.util.MetrixUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.messenger.app.BuildConfig
import com.messenger.app.R
import java.util.*

class ContentGridLayout : GridLayout, ContentHolder {
    private val contents = ArrayList<IMediaContent>()
    private var paddings = 0

    override var contentClickListener: ContentClickListener? = null


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun clearContent() {
        contents.clear()
    }

    fun addContent(content: IMediaContent) {
        if (contents.size >= 10) {
            contents.removeAt(0)
        }
        contents.add(content)
    }

    fun reGroup() {
        removeAllViews()
        when (contents.size) {
            1 -> {
                rowCount = 1
                columnCount = 1
                val view = createViewForContent(contents[0])
                val params = LayoutParams()
                params.columnSpec = spec(UNDEFINED, 1f)
                params.rowSpec = spec(UNDEFINED, 1f)
                addView(view, params)
                requestLayout()
            }
            2 -> {
                rowCount = 1
                columnCount = 2
                var i = 0
                while (i < 2) {
                    val view = createViewForContent(contents[i])
                    val params = view.layoutParams as LayoutParams
                    params.columnSpec = spec(UNDEFINED, 1f)
                    params.rowSpec = spec(UNDEFINED, 1f)
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            3, 4 -> {
                columnCount = 2
                rowCount = contents.size - 1
                var view = createViewForContent(contents[0])
                var params = view.layoutParams as LayoutParams
                params.columnSpec = spec(UNDEFINED, 1, contents.size - 1.toFloat())
                params.rowSpec = spec(UNDEFINED, contents.size - 1, contents.size - 1.toFloat())
                view.requestLayout()
                addView(view)
                var i = 1
                while (i < contents.size) {
                    view = createViewForContent(contents[i])
                    params = view.layoutParams as LayoutParams
                    params.columnSpec = spec(UNDEFINED, 1, 1f)
                    params.rowSpec = spec(UNDEFINED, 1, 1f)
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            5 -> {
                columnCount = 3
                rowCount = 3
                var view = createViewForContent(contents[0])
                var params = view.layoutParams as LayoutParams
                params.columnSpec = spec(UNDEFINED, 2, 1f)
                params.rowSpec = spec(UNDEFINED, 2, 2f)
                view.requestLayout()
                addView(view)
                view = createViewForContent(contents[1])
                params = view.layoutParams as LayoutParams
                params.columnSpec = spec(UNDEFINED, 1, 1f)
                params.rowSpec = spec(UNDEFINED, 2, 2f)
                view.requestLayout()
                addView(view)
                var i = 2
                while (i < 5) {
                    view = createViewForContent(contents[i])
                    params = view.layoutParams as LayoutParams
                    params.columnSpec = spec(UNDEFINED, 1, 1f)
                    params.rowSpec = spec(UNDEFINED, 1, 1f)
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            6 -> {
                columnCount = 3
                rowCount = 3
                var view = createViewForContent(contents[0])
                var params = view.layoutParams as LayoutParams
                params.columnSpec = spec(UNDEFINED, 2, 1f)
                params.rowSpec = spec(UNDEFINED, 2, 2f)
                view.requestLayout()
                addView(view)
                var i = 1
                while (i < 6) {
                    view = createViewForContent(contents[i])
                    params = view.layoutParams as LayoutParams
                    params.columnSpec = spec(UNDEFINED, 1, 1f)
                    params.rowSpec = spec(UNDEFINED, 1, 1f)
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            7 -> {
                columnCount = 3
                rowCount = 4
                var view = createViewForContent(contents[0])
                var params = view.layoutParams as LayoutParams
                params.columnSpec = spec(UNDEFINED, 3, 1f)
                params.rowSpec = spec(UNDEFINED, 3, 3f)
                view.requestLayout()
                addView(view)
                var i = 1
                while (i < 7) {
                    view = createViewForContent(contents[i])
                    params = view.layoutParams as LayoutParams
                    params.columnSpec = spec(UNDEFINED, 1, 1f)
                    params.rowSpec = spec(UNDEFINED, 1, 1f)
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            8 -> {
                columnCount = 2
                rowCount = 4
                var i = 0
                while (i < 8) {
                    val view = createViewForContent(contents[i])
                    val params = view.layoutParams as LayoutParams
                    params.columnSpec = spec(UNDEFINED, 1, 1f)
                    params.rowSpec = spec(UNDEFINED, 1, 1f)
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            9 -> {
                columnCount = 6
                rowCount = 4
                var i = 0
                while (i < 9) {
                    val view = createViewForContent(contents[i])
                    val params = view.layoutParams as LayoutParams
                    if (i < 6) {
                        params.columnSpec = spec(UNDEFINED, 3, 3f)
                        params.rowSpec = spec(UNDEFINED, 1, 2f)
                    } else {
                        params.columnSpec = spec(UNDEFINED, 2, 2f)
                        params.rowSpec = spec(UNDEFINED, 1, 1f)
                    }
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            10 -> {
                columnCount = 6
                rowCount = 4
                var i = 0
                while (i < 10) {
                    val view = createViewForContent(contents[i])
                    val params = view.layoutParams as LayoutParams
                    if (i < 4) {
                        params.columnSpec = spec(UNDEFINED, 3, 3f)
                        params.rowSpec = spec(UNDEFINED, 1, 2f)
                    } else {
                        params.columnSpec = spec(UNDEFINED, 2, 2f)
                        params.rowSpec = spec(UNDEFINED, 1, 1f)
                    }
                    view.requestLayout()
                    addView(view)
                    i++
                }
            }
            else -> if (BuildConfig.DEBUG) {
                Log.d(TAG, "Invalid content count: " + contents.size)
            }
        }
    }

    private fun init(context: Context) {
        orientation = HORIZONTAL
        paddings = MetrixUtil.dpToPx(context, 1)
    }

    private fun createViewForContent(content: IMediaContent): View {
        val view = when (content.type) {
            IMediaContent.IMAGE -> {
                val view = ImageView(context)
                view.transitionName = resources.getString(R.string.util_transition_image_fullscreen)
                Glide.with(this)
                        .load(content.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(view)
                view.setOnClickListener { v: View? ->
                    if (contentClickListener != null) {
                        contentClickListener!!.onImageClick(view, content)
                    }
                }
                view
            }
            IMediaContent.VIDEO -> {
                val view = PlayerView(context)
                view.useArtwork = true
                view.defaultArtwork = AppCompatResources.getDrawable(context, R.drawable.logo512)
                view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                view.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                val player = SimpleExoPlayer.Builder(context).build()
                view.player = player
                player.addListener(object : Player.EventListener {
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_IDLE) {
                            contentClickListener?.onVideoClick(view, content)
                            player.stop()
                        }
                    }
                })
                val mediaItem = MediaItem.fromUri(Uri.parse(content.url))
                player.setMediaItem(mediaItem)
                player.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                view.transitionName = context.getString(R.string.util_transition_video_fullscreen)
                view.setOnClickListener {
                    contentClickListener?.onVideoClick(view, content)
                }
                view
            }
            else -> throw IllegalArgumentException("Invalid MediaContent type: ${content.type}")
        }
        val params = LayoutParams()
        params.width = 0
        params.height = 0
        view.setPadding(paddings, paddings, paddings, paddings)
        view.layoutParams = params
        view.isClickable = true
        view.isFocusable = true
        view.isLongClickable = true
        return view
    }

    fun setFullscreenTransition(transitionActivity: Activity?) {
        contentClickListener = object : ContentClickListener {
            override fun onImageClick(view: ImageView, content: IMediaContent) {
                if (transitionActivity != null) {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            transitionActivity, view, context.getString(R.string.util_transition_image_fullscreen))
                    FullscreenImageActivity.startActivity(transitionActivity, content.url, options.toBundle())
                }
            }

            override fun onVideoClick(playerView: PlayerView, content: IMediaContent) {
                if (transitionActivity != null) {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            transitionActivity, playerView, context.getString(R.string.util_transition_video_fullscreen))
                    FullscreenVideoActivity.startActivity(transitionActivity, content.url, options.toBundle())
                }
            }
        }
    }

    companion object {
        private val TAG = ContentGridLayout::class.java.simpleName
    }
}