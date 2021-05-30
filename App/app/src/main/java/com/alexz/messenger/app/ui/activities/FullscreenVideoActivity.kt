//package com.alexz.messenger.app.ui.activities
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.google.android.exoplayer2.MediaItem
//import com.google.android.exoplayer2.SimpleExoPlayer
//import com.google.android.exoplayer2.ui.PlayerView
//import com.messenger.app.R
//
//class FullscreenVideoActivity : AppCompatActivity() {
//
//    private var player: SimpleExoPlayer? = null
//    private var autoplay = true
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_fullscreen_video)
//        val uri = intent.getStringExtra(EXTRA_URI)
//        val playerView = findViewById<PlayerView>(R.id.player_fullscreen)
//        player = SimpleExoPlayer.Builder(this).build()
//        playerView.player = player
//        val mediaItem = MediaItem.fromUri(Uri.parse(uri))
//        player?.setMediaItem(mediaItem)
//        player?.prepare()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        player?.let {
//            outState.putLong(EXTRA_POS, it.contentPosition)
//            outState.putInt(EXTRA_WINDOW, it.currentWindowIndex)
//            outState.putBoolean(EXTRA_AUTOPLAY, it.playWhenReady)
//        }
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        player?.seekTo(savedInstanceState.getInt(EXTRA_WINDOW),
//                savedInstanceState.getLong(EXTRA_POS, 0))
//        autoplay = savedInstanceState.getBoolean(EXTRA_AUTOPLAY).or(false)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        player?.stop()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        player?.playWhenReady = autoplay
//    }
//
//    companion object {
//        private const val EXTRA_URI = "EXTRA_URI"
//        private const val EXTRA_POS = "EXTRA_POS"
//        private const val EXTRA_WINDOW = "EXTRA_WINDOW"
//        private const val EXTRA_AUTOPLAY = "EXTRA_AUTOPLAY"
//        fun startActivity(context: Context, videoUri: String?, bundle: Bundle?) {
//            val intent = Intent(context, FullscreenVideoActivity::class.java)
//            intent.putExtra(EXTRA_URI, videoUri)
//            context.startActivity(intent)
//        }
//    }
//}