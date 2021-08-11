package com.community.messenger.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.community.messenger.app.R

class DialogFloatingButton : FloatingActionButton {
    private var moving = false
    private var hide: Animation? = null
    private var show: Animation? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        moving = false
        hide = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fb_hide)
        hide?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                moving = true
            }

            override fun onAnimationEnd(animation: Animation) {
                visibility = View.INVISIBLE
                moving = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        hide?.fillAfter = true
        show = AnimationUtils.loadAnimation(getContext(), R.anim.anim_fb_show)
        show?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                moving = true
                visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                moving = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        show?.fillBefore = true
    }

    override fun hide() {
        if (!moving && visibility == View.VISIBLE) {
            startAnimation(hide)
        }
    }

    override fun show() {
        if (!moving && visibility == View.INVISIBLE) {
            startAnimation(show)
        }
    }
}