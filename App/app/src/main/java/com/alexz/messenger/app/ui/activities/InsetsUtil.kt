package com.alexz.test

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat

typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

fun removeSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
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
        listener: OnSystemInsetsChangedListener
): Int {
    val hasKeyboard = view.isKeyboardAppeared(bottomInset)
    val desiredBottomInset = if (hasKeyboard) bottomInset else 0
    listener(topInset, if (hasKeyboard) 0 else bottomInset)
    return desiredBottomInset
}

fun View.isKeyboardAppeared(bottomInset: Int) =
        bottomInset / resources.displayMetrics.heightPixels.toDouble() > .25

@TargetApi(21)
fun Activity.setWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    removeSystemInsets(window.decorView, listener)
    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
}
