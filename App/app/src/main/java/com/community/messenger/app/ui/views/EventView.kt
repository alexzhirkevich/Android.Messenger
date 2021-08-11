package com.community.messenger.app.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.community.messenger.app.R
import kotlin.math.round

class EventView @JvmOverloads constructor(context  : Context, attrs : AttributeSet?=null, defStyle :  Int = 0 )
    : View(context,attrs,defStyle) {

    private val NAME_LEN_MAX = resources.getInteger(R.integer.event_name_len_max)
    private val NAME_LEN_MAX_STR = buildString {
        repeat(NAME_LEN_MAX)
        append('W')
    }

    interface Values {
        var name: String
        var description: String
        var address: String
        var time: Long
        var isFavorite : Boolean
    }

    interface TextColors {
        var nameTextColor: Int
        var descriptionTextColor: Int
        var dateTextColor: Int
        var timeTextColor: Int
        var addressTextColor: Int
    }

    interface TextSizes{
        var nameTextSize: Float
        var descriptionTextSize: Float
        var dateTextSize: Float
        var timeTextSize: Float
        var addressTextSize: Float
    }

    val values : Values = ValuesImp()
    val textColors : TextColors = TextColorsImp()
    var textSizes : TextSizes = TextSizesImp()

    private val bmpStar = ContextCompat.getDrawable(context,R.drawable.ic_star)!!.toBitmap()
    private val bmpStarOutlined = ContextCompat.getDrawable(context,R.drawable.ic_star_outline)!!.toBitmap()

    private val eventBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val descriptionBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val namePaint =  Paint(Paint.ANTI_ALIAS_FLAG)
    private val descriptionPaint =  Paint(Paint.ANTI_ALIAS_FLAG)
    private val datePaint =  Paint(Paint.ANTI_ALIAS_FLAG)
    private val timePaint =  Paint(Paint.ANTI_ALIAS_FLAG)
    private val addressPaint =  Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val fontMetrics = namePaint.fontMetrics

       // val maxTextWidth = namePaint.measureText()

    }

    private inner class ValuesImp : Values{
        override var name: String = ""
            set(value) {
                field = value
                invalidate()
            }
        override var description: String = ""
            set(value) {
                field = value
                invalidate()
            }
        override var address: String  = ""
            set(value) {
                field = value
                invalidate()
            }
        override var time: Long = System.currentTimeMillis()
            set(value) {
                field = value
                invalidate()
            }
        override var isFavorite: Boolean = false
            set(value) {
                field = value
                invalidate()
            }
    }

    inner class TextColorsImp : TextColors {
        override var nameTextColor: Int = ContextCompat.getColor(context, R.color.white)
            set(value) {
                field = value
                namePaint.color = value
                invalidate()
            }
        override var descriptionTextColor: Int = ContextCompat.getColor(context, R.color.gray)
            set(value) {
                field = value
                descriptionPaint.color = value
                invalidate()
            }
        override var dateTextColor: Int = ContextCompat.getColor(context, R.color.red)
            set(value) {
                field = value
                datePaint.color = value
                invalidate()
            }
        override var timeTextColor: Int = ContextCompat.getColor(context, R.color.blue)
            set(value) {
                field = value
                timePaint.color = value
                invalidate()
            }
        override var addressTextColor: Int = ContextCompat.getColor(context, R.color.green)
            set(value) {
                field = value
                addressPaint.color = value
                invalidate()
            }
    }

    inner class TextSizesImp : TextSizes {
        override var nameTextSize: Float = resources.getDimension(R.dimen.font_size_big)
            set(value) {
                field = value
                namePaint.textSize = round(value * resources.displayMetrics.scaledDensity)
                invalidate()
            }
        override var descriptionTextSize: Float = resources.getDimension(R.dimen.font_size_small)
            set(value) {
                field = value
                descriptionPaint.textSize = round(value * resources.displayMetrics.scaledDensity)
                invalidate()
            }
        override var dateTextSize: Float = resources.getDimension(R.dimen.font_size_small)
            set(value) {
                field = value
                datePaint.textSize = round(value * resources.displayMetrics.scaledDensity)
                invalidate()
            }
        override var timeTextSize: Float = resources.getDimension(R.dimen.font_size_small)
            set(value) {
                field = value
                timePaint.textSize = round(value * resources.displayMetrics.scaledDensity)
                invalidate()
            }
        override var addressTextSize: Float = resources.getDimension(R.dimen.font_size_small)
            set(value) {
                field = value
                addressPaint.textSize = round(value * resources.displayMetrics.scaledDensity)
                invalidate()
            }
    }
}