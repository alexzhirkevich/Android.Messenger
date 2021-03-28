package com.alexz.messenger.app.ui.common.contentgridlayout

import android.widget.ImageView
import com.alexz.messenger.app.data.model.interfaces.IMediaContent
import com.google.android.exoplayer2.ui.PlayerView

interface ContentClickListener {
    fun onImageClick(view: ImageView, content: IMediaContent)
    fun onVideoClick(playerView: PlayerView, content: IMediaContent)
}