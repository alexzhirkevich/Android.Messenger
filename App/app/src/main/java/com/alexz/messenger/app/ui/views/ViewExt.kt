package com.alexz.messenger.app.ui.views

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.alexz.messenger.app.util.KeyboardUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun View.setTopMargin(margin : Int){
    (layoutParams as ViewGroup.MarginLayoutParams).topMargin = margin
    requestLayout()
}

fun View.setBottomMargin(margin : Int){
    (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = margin
    requestLayout()
}


fun Button.enableWithEditTexts(@ColorRes enableTextClr : Int, @ColorRes disabledTextClr: Int,
                               isEnabled : () -> Boolean, vararg companions: EditText) {
    fun check() = run {
            this.isEnabled = isEnabled()
            setTextColor(ContextCompat.getColor(context, if (this.isEnabled) enableTextClr else disabledTextClr))
    }

    companions.forEach {
        it.removeCallbacks {  }
        it.doOnTextChanged { _, _, _, _ ->
            check()
        }
    }
    check()
}

fun FloatingActionButton.enableWithEditTexts(@ColorRes enableTextClr : Int, @ColorRes disabledTextClr: Int,
                               isEnabled : () -> Boolean, vararg companions: EditText) {
    fun check() = run {
        this.isEnabled = isEnabled()
        foregroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context,if (this.isEnabled) enableTextClr else disabledTextClr))
    }

    companions.forEach {
        it.removeCallbacks {  }
        it.doOnTextChanged { _, _, _, _ ->
            check()
        }
    }
    check()
}

fun FloatingActionButton.animateColor(start : Int, end: Int, duration:Long){
    ObjectAnimator.ofInt(start,end).apply {
        this.duration = duration
        setEvaluator(ArgbEvaluator())
        addUpdateListener {
            backgroundTintList = ColorStateList.valueOf(it.animatedValue as Int)
        }
    }.start()
}

fun TextView.focus(){
    isEnabled = true
    requestFocus()
    if (!KeyboardUtils.hasHardwareKeyboard(context)){
        KeyboardUtils.showKeyboard(this)
    }
}

fun TextView.unFocus(){
    isEnabled = false
    clearFocus()
    if (!KeyboardUtils.hasHardwareKeyboard(context)){
        KeyboardUtils.hideKeyboard(this)
    }
}
