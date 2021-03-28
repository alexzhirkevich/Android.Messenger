package com.alexz.messenger.app.ui.views

import android.Manifest
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.annotation.RequiresPermission
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.messenger.app.BuildConfig
import com.messenger.app.R

class AvatarImageView : AppCompatImageView {
    private var circle: Drawable? = null
    var imageUri: String? = null
        private set

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context?) {
        if (circle == null) {
            circle = AppCompatResources.getDrawable(context!!, R.drawable.drawable_circle)
        }
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun setImageURI(uri: Uri?) {
        if (uri != null) {
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
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        if (circle == null) {
            circle = AppCompatResources.getDrawable(context, R.drawable.drawable_circle)
        }
        try {
            super.setImageDrawable(LayerDrawable(arrayOf(drawable, circle)))
        } catch (e: Exception) {
            super.setImageDrawable(drawable)
        }
    }
}