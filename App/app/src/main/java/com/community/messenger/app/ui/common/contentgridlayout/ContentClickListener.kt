package com.community.messenger.app.ui.common.contentgridlayout

import android.widget.ImageView
import com.community.messenger.common.entities.interfaces.IMediaContent
import com.google.android.exoplayer2.ui.PlayerView

interface ContentClickListener {
    fun onImageClick(view: ImageView, content: IMediaContent)
    fun onVideoClick(playerView: PlayerView, content: IMediaContent)
}