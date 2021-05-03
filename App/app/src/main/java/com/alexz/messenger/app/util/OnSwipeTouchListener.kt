package com.alexz.messenger.app.util

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class OnSwipeTouchListener(ctx: Context?) : OnTouchListener {

    var onMoveRight : (delta :Float) -> Unit = {}
    var onMoveLeft : (delta :Float) -> Unit = {}
    var onMoveTop : (delta :Float) -> Unit = {}
    var onMoveBottom : (delta :Float) -> Unit = {}

    private val gestureDetector = GestureDetector(ctx, GestureListener())

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (distanceX > 0) {
                onMoveRight(distanceX)
            } else if (distanceX < 0) {
                onMoveLeft(-distanceX)
            }

            if (distanceY > 0) {
                onMoveTop(distanceY)
            } else if (distanceY < 0) {
                onMoveBottom(-distanceY)
            }
            return true
        }
    }
}