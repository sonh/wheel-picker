package com.sonhvp.wheelpicker.utils

import android.view.GestureDetector
import android.view.MotionEvent

fun gestureListener(
    onDown: (e: MotionEvent?) -> Boolean = { false },
    onScroll: (e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) -> Boolean = { _, _, _, _ -> false },
    onFling: (e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float) -> Boolean = { _, _, _, _ -> false },
    onSingleTapConfirmed: (e: MotionEvent?) -> Boolean = { false }
) =
    object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean = onDown(e)
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean = onScroll(e1, e2, distanceX, distanceY)
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean = onFling(e1, e2, velocityX, velocityY)
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean = onSingleTapConfirmed(e)
    }
