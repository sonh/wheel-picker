package com.sonhvp.wheelpicker

import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.sonhvp.wheelpicker.utils.gestureListener
import com.sonhvp.wheelpicker.utils.initScaledMinimumFlingVelocity

abstract class WheelPicker(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    protected val scrollingSpeed = 1f
    private val minimumFlingVelocity = initScaledMinimumFlingVelocity()

    var debug: Boolean = true

    enum class Direction {
        TOP, BOTTOM, NONE
    }

    fun OverScroller.forceFinishScroll(): Boolean = this.currVelocity <= minimumFlingVelocity

    fun Int.dpToPx(): Float = context.resources.displayMetrics.density
    fun Float.dpToPx(): Float = context.resources.displayMetrics.density

    /*fun logGesture() {
        log("currentScrollDirection: $currentScrollDirection\n" +
                "currentFlingDirection: $currentFlingDirection\n" +
                "")
    }*/

    fun log(msg: String) { if (BuildConfig.DEBUG && debug) Log.d("WheelPicker", msg) }

}

infix fun Paint.getTextWidth(text: String): Float {
    val rect = Rect()
    getTextBounds(text, 0, text.length, rect)
    return rect.width().toFloat()
}

infix fun Paint.getTextHeight(text: String): Float {
    val rect = Rect()
    getTextBounds(text, 0, text.length, rect)
    return rect.height().toFloat()
}