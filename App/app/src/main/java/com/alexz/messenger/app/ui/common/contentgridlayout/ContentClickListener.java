package com.alexz.messenger.app.ui.common.contentgridlayout;

import android.widget.ImageView;

import com.alexz.messenger.app.data.model.interfaces.IMediaContent;
import com.google.android.exoplayer2.ui.PlayerView;

public interface ContentClickListener {

    void onImageClick(ImageView view, IMediaContent content);

    void onVideoClick(PlayerView playerView, IMediaContent content);
}
