package com.community.messenger.app.ui.adapters.recycler

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.community.messenger.app.R
import com.community.messenger.common.entities.interfaces.IMediaContent
import com.community.recadapter.BaseViewHolder
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.android.exoplayer2.ui.PlayerView

class FullscreenContentAdapter : com.community.recadapter.BaseRecyclerAdapter<IMediaContent, FullscreenContentAdapter.FullscreenContentViewHolder>() {

    override fun onCreateClickableViewHolder(parent: ViewGroup, viewType: Int): FullscreenContentViewHolder {
        val view = if (viewType == IMediaContent.IMAGE)
            SubsamplingScaleImageView(parent.context).apply {
                setDoubleTapZoomDuration(resources.getInteger(R.integer.anim_duration_medium))
                maxScale = 1.5f
                setDoubleTapZoomScale(maxScale/2)
            }
        else
            PlayerView(parent.context)

        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        return FullscreenContentViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int =
            entities[position].type

    class FullscreenContentViewHolder(view: View) : BaseViewHolder<IMediaContent>(view) {

        override fun onBind(entity: IMediaContent) {

            if (entity.type == IMediaContent.IMAGE && itemView is SubsamplingScaleImageView) {

                Glide.with(itemView.context).asBitmap().load(entity.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .addListener(object : RequestListener<Bitmap> {
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?,
                                                      isFirstResource: Boolean): Boolean {
                                Log.e(this.javaClass.simpleName, "Failed to load image\n${e.toString()}")
                                return true
                            }

                            override fun onResourceReady(resource: Bitmap?, model: Any?,
                                                         target: Target<Bitmap>?, dataSource: DataSource?,
                                                         isFirstResource: Boolean): Boolean {
                                if (resource != null) {
                                    itemView.post {
                                        itemView.setImage(ImageSource.bitmap(resource))
                                    }
                                    return true

                                }
                                return false
                            }
                        }).submit()
            }
        }
    }
}