package com.alexz.messenger.app.ui.views

import android.Manifest
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.messenger.app.R
import kotlin.math.roundToInt


class AvatarImageView : AppCompatImageView {
    var imageUri: Uri? = null
        private set

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    init {
        val padding = dpToPx(1)
        setPadding(padding,padding,padding,padding)
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    @RequiresPermission(Manifest.permission.INTERNET)
    override fun setImageURI(uri: Uri?) {
        setBackgroundResource(R.drawable.drawable_circle)
        foreground = null
        if (uri != null) {
            Glide.with(this).load(uri).circleCrop().into(this)
            imageUri = uri
        }
    }
    
    fun setupWithText(text : String,@DimenRes fontSize: Int, @ColorRes color : Int){
        val cut = text.split(" ").take(2).map { it[0] }.joinToString(separator = "").toUpperCase()
        setBackgroundResource(R.drawable.drawable_circle)
        foreground = TextDrawable(resources,cut,ContextCompat.getColor(context, color),resources.getDimension(fontSize))

        //setImageDrawable()
    }

    class TextDrawable(
            res: Resources,
            private val mText: CharSequence,
            textColor: Int,
            private val fontSize : Float) : Drawable() {
        private val mPaint: Paint
        private val mIntrinsicWidth: Int
        private val mIntrinsicHeight: Int

        override fun draw(canvas: Canvas) {
            val bounds = bounds
            canvas.drawText(mText, 0, mText.length,
                    bounds.centerX().toFloat(), bounds.centerY().toFloat() - mPaint.ascent() / 2.5f, mPaint)
        }

        override fun getOpacity(): Int {
            return mPaint.alpha
        }

        override fun getIntrinsicWidth(): Int {
            return mIntrinsicWidth
        }

        override fun getIntrinsicHeight(): Int {
            return mIntrinsicHeight
        }

        override fun setAlpha(alpha: Int) {
            mPaint.alpha = alpha
        }

        override fun setColorFilter(filter: ColorFilter?) {
            mPaint.colorFilter = filter
        }

        init {
            mPaint = Paint()
            mPaint.color = textColor
            mPaint.isFakeBoldText = true
            mPaint.isAntiAlias = true
            mPaint.textAlign = Align.CENTER
            val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    fontSize, res.displayMetrics)
            mPaint.textSize = textSize
            mIntrinsicWidth = (mPaint.measureText(mText, 0, mText.length)+.5).toInt()
            mIntrinsicHeight = mPaint.getFontMetricsInt(null)
        }
    }
}