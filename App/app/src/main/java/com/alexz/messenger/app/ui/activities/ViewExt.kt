package com.alexz.messenger.app.ui.activities

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.widget.Button
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun Button.enableWithEditTexts(@ColorRes enableTextClr : Int, @ColorRes disabledTextClr: Int,
                               additionalBlocker : () -> Boolean, vararg companions: EditText) {
    var enable = true
    companions.forEach {
        it.doOnTextChanged { _, _, _, _ ->
            isEnabled = companions.all { c -> c.text.isNotEmpty() } && additionalBlocker()
            setTextColor(ContextCompat.getColor(context,if (isEnabled) enableTextClr else disabledTextClr))
        }
        if (it.text.isEmpty() || !additionalBlocker()){
            enable = false
            if (isEnabled) {
                isEnabled = false
                setTextColor(ContextCompat.getColor(context, disabledTextClr))
            }
        }
    }
    if (enable){
        isEnabled = true
        setTextColor(ContextCompat.getColor(context, enableTextClr))

    }
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