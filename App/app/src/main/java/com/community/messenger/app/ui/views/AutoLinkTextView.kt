package com.community.messenger.app.ui.views

import android.content.Context
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import com.community.messenger.app.R
import com.vanniktech.emoji.EmojiTextView

class AutoLinkTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : EmojiTextView(context, attrs, defStyleAttr) {

    private val linksColor = ContextCompat.getColor(context, R.color.link)

    private var onClickListener: Link.OnClickListener = object : Link.OnClickListener {
        override fun onClick(clickedText: String) {}
    }

    private var onLongClickListener: Link.OnLongClickListener = object : Link.OnLongClickListener {
        override fun onLongClick(clickedText: String) {}
    }

    fun setLinkClickListener(listener: (String) -> Unit) {
        onClickListener = object : Link.OnClickListener {
            override fun onClick(clickedText: String) {
                listener(clickedText)
            }
        }
        text = text
    }

    fun setLinkLongClickListener(listener: (String) -> Unit) {
        onLongClickListener = object : Link.OnLongClickListener {
            override fun onLongClick(clickedText: String) {
                listener(clickedText)
            }
        }
        text = text
    }


    init {
        linksClickable = true
        autoLinkMask = Linkify.ALL
        setLinkTextColor(linksColor)
        movementMethod = LinkMovementMethod()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text != null) {
            val links = text.split(" ","\n","\t").filter { it.startsWith("@") }.map {
                val link = Link(it)
                        .setTextColor(linksColor)
                        .setUnderlined(false)
                        .setBold(false)
                        .setUnderlined(false)
                        .setHighlightAlpha(0.4f)
                        .setTextColorOfHighlightedLink(Color.BLACK)
                if (onClickListener != null) {
                    link.setOnClickListener(onClickListener)
                }
                if (onLongClickListener != null){
                    link.setOnLongClickListener(onLongClickListener)
                }
                link
            }
            if (links.isNotEmpty()) {
                return super.setText(LinkBuilder.from(context, text).addLinks(links).build(), type)
            }
        }
        super.setText(text, type)
    }
}