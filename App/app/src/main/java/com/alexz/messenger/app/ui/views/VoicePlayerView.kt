package com.alexz.messenger.app.ui.views

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import com.messenger.app.R

class VoicePlayerView(context: Context) : RelativeLayout(context) {
    var btnPlay: ImageButton? = null
    fun play() {
        btnPlay?.setImageResource(R.drawable.ic_play)
    }

    fun pause() {
        btnPlay?.setImageResource(R.drawable.ic_pause)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.item_voice_player, this)
        btnPlay = findViewById(R.id.btn_voice_play)
    }

    init {
        init(context)
    }
}