package com.community.messenger.app.ui.activities

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat

interface SystemInsetsChangedListener{
    fun onApplyWindowInsets(statusBarSize: Int, navigationBarSize: Int)
}
fun removeSystemInsets(view: View, listener: SystemInsetsChangedListener) {
    ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->

        val desiredBottomInset = calculateDesiredBottomInset(
                view,
                insets.systemWindowInsetTop,
                insets.systemWindowInsetBottom,
                listener
        )

        ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, desiredBottomInset)
        )
    }

}

fun calculateDesiredBottomInset(
        view: View,
        topInset: Int,
        bottomInset: Int,
        listener: SystemInsetsChangedListener
): Int {
    val hasKeyboard = view.isKeyboardAppeared(bottomInset)
    val desiredBottomInset = if (hasKeyboard) bottomInset else 0
    listener.onApplyWindowInsets(topInset, if (hasKeyboard) 0 else bottomInset)
    return desiredBottomInset
}

fun View.isKeyboardAppeared(bottomInset: Int) =
        bottomInset / resources.displayMetrics.heightPixels.toDouble() > .25

fun Activity.setWindowTransparency(listener: SystemInsetsChangedListener) {
    removeSystemInsets(window.decorView, listener)
    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.requestUpdateInsets () =  ViewCompat.requestApplyInsets(window.decorView)
