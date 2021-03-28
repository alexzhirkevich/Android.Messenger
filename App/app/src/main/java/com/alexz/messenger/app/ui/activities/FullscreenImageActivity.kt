package com.alexz.messenger.app.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.messenger.app.R

class FullscreenImageActivity : AppCompatActivity() {

    private var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val uri = intent.getStringExtra(EXTRA_IMAGE_URI)
        imageView = findViewById(R.id.imageview_fullscreen)
        imageView?.let {Glide.with(this).load(uri).into(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return true
    }

    companion object {
        private const val EXTRA_IMAGE_URI = "IMAGE_URI"
        fun startActivity(context: Context, imageUri: String?, bundle: Bundle?) {
            val intent = Intent(context, FullscreenImageActivity::class.java)
            intent.putExtra(EXTRA_IMAGE_URI, imageUri)
            if (bundle == null) {
                context.startActivity(intent)
            } else {
                context.startActivity(intent, bundle)
            }
        }
    }
}