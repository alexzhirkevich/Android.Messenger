package com.alexz.messenger.app.ui.views

import android.Manifest
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.messenger.app.BuildConfig

class AvatarImageView : AppCompatImageView {
    var imageUri: String? = null
        private set

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun setImageURI(uri: Uri?) {
        if (uri != null) {
            try {
                Glide.with(this).load(uri).circleCrop().addListener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        if (BuildConfig.DEBUG && e != null) e.printStackTrace()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        setImageDrawable(resource)
                        return true
                    }
                }).diskCacheStrategy(DiskCacheStrategy.ALL).submit()
                imageUri = uri.toString()
            }catch (ignore : Exception){}
        }
    }
}