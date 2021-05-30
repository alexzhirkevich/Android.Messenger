package com.alexz.messenger.app.ui.views

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.messenger.app.R
import rm.com.audiowave.AudioWaveView
import kotlin.random.Random

class VoiceView : RelativeLayout {

    var isPaused = true
        private set

    var isPrepared = false

    var length = 0L
        set(value){
            field = value
            if (isPrepared) {
                stop()
                progressAnim.duration = value
            }
        }

    var uri : Uri? = null
    set(value){
        field = value
        if (isPrepared) {
            stop()
            mediaPlayer.reset()
            isPrepared = false
            prepareAsync()
        }
    }
    private val button: ImageButton
    private val wave: AudioWaveView
    private val layout:ViewGroup
    private val mediaPlayer = MediaPlayer()
    private val timer : TextView

    private val progressAnim: ObjectAnimator

    private var isPreparing = false
    private var curPlayTime = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes)

    override fun setBackground(background: Drawable?) {
        layout.background = background
    }

    init {
        inflate(context, R.layout.item_voice_player, this)
        length = 3500
        button = findViewById(R.id.btn_voice_play)
        wave = findViewById(R.id.view_voice_visualizer)
        timer = findViewById(R.id.text_voice_timer)
        layout = findViewById(R.id.voice_layout)

        button.setOnClickListener {
            if (isPaused) {
                play()
            } else {
                pause()
            }
        }

        wave.setRawData(Random.nextBytes(1024))

        mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        )

        progressAnim = ObjectAnimator.ofFloat(wave, "progress", 0F, 100F).apply {
            interpolator = LinearInterpolator()
            duration = length

        }

        wave.onStartTracking = { progress ->
            curPlayTime = (length * progress / 100.0).toInt()
            progressAnim.currentPlayTime = curPlayTime.toLong()
            if (mediaPlayer.isPlaying) {
                mediaPlayer.seekTo(curPlayTime)
            } else {
                play()
            }
        }
        wave.onProgressChanged = { p, b ->
            val sec = progressAnim.currentPlayTime/1000
            timer.text = buildString {
                append(sec / 60)
                append(':')
                if (sec%60<10){
                    append("0")
                }
                append(sec % 60)
            }

            if (p >= 99f) {
                stop()
            }
        }
    }

    fun play() {
        if (isPaused) {
            button.setImageResource(R.drawable.ic_pause)
            if (isPrepared) {
                mediaPlayer.seekTo(curPlayTime)
                if (progressAnim.isStarted) {
                    progressAnim.resume()
                } else{
                    progressAnim.start()
                }
                mediaPlayer.start()
            } else {
                if (!isPreparing) {
                    prepareAsync()
                }
            }
            isPaused = false
        }
    }

    fun pause() {
        if (!isPaused) {
            button.setImageResource(R.drawable.ic_play)
            progressAnim.pause()
            mediaPlayer.pause()
            curPlayTime = mediaPlayer.currentPosition
            isPaused = true
        }
    }

    private fun prepareAsync() {
        isPreparing = true
        mediaPlayer.setDataSource(context, uri!!)
        mediaPlayer.setOnPreparedListener {
            isPreparing = false
            isPrepared = true
            mediaPlayer.seekTo(curPlayTime.toInt())
            if (!isPaused) {
                mediaPlayer.seekTo(curPlayTime.toInt())
                mediaPlayer.start()
                if (!progressAnim.isStarted) {
                    progressAnim.start()
                } else {
                    progressAnim.resume()
                }
            }
        }
        try {
            mediaPlayer.prepareAsync()
        }catch (ignore: IllegalStateException){}
    }

    @SuppressLint("SetTextI18n")
    private fun stop() {
        button.setImageResource(R.drawable.ic_play)
        mediaPlayer.pause()
        progressAnim.cancel()
        progressAnim.currentPlayTime = 0
        curPlayTime = 0
        mediaPlayer.seekTo(0)
        timer.text = "0:00"
        isPaused = true
    }


}