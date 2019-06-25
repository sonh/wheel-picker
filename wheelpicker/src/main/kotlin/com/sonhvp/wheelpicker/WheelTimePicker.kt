package com.sonhvp.wheelpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.OverScroller
import androidx.core.view.GestureDetectorCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.sonhvp.wheelpicker.utils.gestureListener
import kotlin.math.abs
import kotlin.math.roundToInt

class WheelTimePicker(ctx: Context, attrs: AttributeSet) : WheelPicker(ctx, attrs) {

    private val gestureDetector: GestureDetectorCompat
    private val hourOverScroller: OverScroller = OverScroller(ctx, FastOutLinearInInterpolator())
    private val minuteOverScroller: OverScroller = OverScroller(ctx, FastOutLinearInInterpolator())

    //Gesture
    private var hourScrollDirection: Direction = Direction.NONE
    private var hourFlingDirection: Direction = Direction.NONE
    private var minuteScrollDirection: Direction = Direction.NONE
    private var minuteFlingDirection: Direction = Direction.NONE

    private var hourOriginY: Int = 0
        set(value) {
            field = when {
                value > hourMaxY -> hourMaxY
                value < hourMinY -> hourMinY
                else -> value
            }
        }
    private var minuteOriginY: Int = 0
        set(value) {
            field = when {
                value > minuteMaxY -> minuteMaxY
                value < minuteMinY -> minuteMinY
                else -> value
            }
        }

    private var hourMinY: Int = Int.MIN_VALUE
    private var hourMaxY: Int = Int.MAX_VALUE
    private var minuteMinY: Int = Int.MIN_VALUE
    private var minuteMaxY: Int = Int.MAX_VALUE

    private var hourIndex: Int = 0
    private var minuteIndex: Int = 0

    private var cellWidth: Float = 0f
    private var cellHeight: Float = 0f
    private var cellPadding: Float = 0f

    private var textCenterY: Float = 0f

    private var numberOfRows = 3
    private var numberOfCols = 2

    private val textPaint: Paint
    private val dividerPaint: Paint

    var onTimeSelected: ((hour: Int, minute: Int) -> Unit)? = null

    init {
        ctx.theme.obtainStyledAttributes (
            attrs,
            R.styleable.WheelTimePicker,
            0, 0).apply {
            try {
                textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = getColor(R.styleable.WheelTimePicker_wtp_textColor, Color.BLACK)
                    textAlign = Paint.Align.CENTER
                    textSize = getDimensionPixelSize(R.styleable.WheelTimePicker_wtp_textSize, 20.dpToPx().toInt()).toFloat()
                }
                dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = getColor(R.styleable.WheelTimePicker_wtp_dividerColor, Color.BLACK)
                    strokeWidth = getDimensionPixelSize(R.styleable.WheelTimePicker_wtp_dividerHeight, 2).toFloat()
                }
            } finally {
                recycle()
            }
        }
        //Scrolling detector
        gestureDetector = GestureDetectorCompat(ctx, initGestureListener(), Handler())
    }

    private fun initGestureListener() : GestureDetector.OnGestureListener = gestureListener (
        onDown = { e ->
            //log("onDown")
            e?.x?.run {
                when {
                    isHourPoint() -> if (hourFlingDirection != Direction.NONE) getHourNearestOrigin()
                    isMinutePoint() -> if (minuteFlingDirection != Direction.NONE) getMinuteNearestOrigin()
                }
            }
            true
        },
        onScroll = { e1, _, distanceX, distanceY ->
            //log("onScroll")
            e1?.x?.run {
                when {
                    isHourPoint() -> {
                        when (hourScrollDirection) {
                            Direction.NONE -> hourScrollDirection = if (abs(distanceX) > abs(distanceY)) {
                                Direction.NONE
                            } else {
                                if (distanceY > 0) Direction.TOP else Direction.BOTTOM
                            }
                            Direction.TOP -> if (abs(distanceY) > abs(distanceX) && distanceY < -3) {
                                hourScrollDirection = Direction.BOTTOM
                            }
                            Direction.BOTTOM -> if (abs(distanceY) > abs(distanceX) && distanceY > 3) {
                                hourScrollDirection = Direction.TOP
                            }
                        }
                        when (hourScrollDirection) {
                            Direction.TOP, Direction.BOTTOM -> {
                                hourOriginY -= distanceY.toInt()
                                postInvalidateOnAnimation()
                            }
                            else -> {}
                        }
                    }
                    isMinutePoint() -> {
                        when (minuteScrollDirection) {
                            Direction.NONE -> minuteScrollDirection = if (abs(distanceX) > abs(distanceY)) {
                                Direction.NONE
                            } else {
                                if (distanceY > 0) Direction.TOP else Direction.BOTTOM
                            }
                            Direction.TOP -> if (abs(distanceY) > abs(distanceX) && distanceY < -3) {
                                minuteScrollDirection = Direction.BOTTOM
                            }
                            Direction.BOTTOM -> if (abs(distanceY) > abs(distanceX) && distanceY > 3) {
                                minuteScrollDirection = Direction.TOP
                            }
                        }
                        when (minuteScrollDirection) {
                            Direction.TOP, Direction.BOTTOM -> {
                                minuteOriginY -= distanceY.toInt()
                                postInvalidateOnAnimation()
                            }
                            else -> {}
                        }
                    }
                }
            }
            true
        },
        onFling = { e1, _, _, velocityY ->
            e1?.x?.run {
                when {
                    isHourPoint() -> {
                        hourFlingDirection = hourScrollDirection
                        hourOverScroller.forceFinished(true)
                        hourOverScroller.fling(1, hourOriginY, 0, (velocityY * scrollingSpeed).toInt(), hourMinY, hourMaxY, hourMinY, hourMaxY)
                        postInvalidateOnAnimation()
                    }
                    isMinutePoint() -> {
                        minuteFlingDirection = minuteScrollDirection
                        minuteOverScroller.forceFinished(true)
                        minuteOverScroller.fling(width - 1, minuteOriginY, 0, (velocityY * scrollingSpeed).toInt(), minuteMinY, minuteMaxY, minuteMinY, minuteMaxY)
                        postInvalidateOnAnimation()
                    }
                }
            }
            true
        }
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val textWidth = textPaint getTextWidth  "123"
        val textHeight = textPaint getTextHeight "123"
        textCenterY = (cellHeight / 2) + (textHeight / 3)

        //Calculate width
        val width: Int = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                //log("Width - MeasureSpec.EXACTLY")
                MeasureSpec.getSize(widthMeasureSpec).also {  width ->
                    cellWidth = (width / numberOfCols).toFloat()
                }
            }
            else -> {
                //log("Width - MeasureSpec.ELSE")
                cellWidth = textWidth + (textWidth * 2)
                cellWidth.toInt() * numberOfCols
            }
        }
        //Calculate height
        val height: Int = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> {
                //log("Height - MeasureSpec.EXACTLY")
                MeasureSpec.getSize(heightMeasureSpec).also { height ->
                    cellHeight = (height / numberOfRows).toFloat()
                }
            }
            else -> {
                //log("Height - MeasureSpec.ELSE")
                cellHeight = textHeight + (textHeight * 2)
                cellHeight.toInt() * numberOfRows
            }
        }
        //Set dimension
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        /*log("hourIndex: $hourIndex \t" +
                "minuteIndex: $minuteIndex")*/

        val newHourIndex = -(hourOriginY / cellHeight).roundToInt()
        val newMinuteIndex = -(minuteOriginY / cellHeight).roundToInt()
        if (newHourIndex != hourIndex || newMinuteIndex != minuteIndex) onTimeSelected?.invoke(newHourIndex.indexToHour(), newMinuteIndex.indexToMinute())
        hourIndex = newHourIndex
        minuteIndex = newMinuteIndex

        canvas?.run {
            drawText(":", cellWidth, cellHeight + textCenterY, textPaint)
            for (i in 0 until numberOfRows - 1) {
                drawLine(0f + cellWidth / 7, cellHeight + cellHeight * i, cellWidth - cellWidth / 7, cellHeight + cellHeight * i, dividerPaint)
                drawLine(cellWidth + cellWidth / 7, cellHeight + cellHeight * i, width.toFloat() - cellWidth / 7, cellHeight + cellHeight * i, dividerPaint)
            }
            for (i in hourIndex - 2 .. hourIndex + 2) {
                drawText(i.indexToHour().plusZero(),  cellWidth / 2, (i * cellHeight) + cellHeight + textCenterY + hourOriginY, textPaint)
            }
            for (i in minuteIndex - 2 .. minuteIndex + 2) {
                drawText(i.indexToMinute().plusZero(), cellWidth + cellWidth / 2, (i * cellHeight) + cellHeight + textCenterY + minuteOriginY, textPaint)
            }
        }
    }

    fun getSelectedHour(): Int = hourIndex.indexToHour()
    fun getSelectedMinute(): Int = minuteIndex.indexToMinute()

    private fun Int.indexToHour(): Int = when {
        this < 0 -> (this % -24).takeUnless { it == 0 }?.let { return@let it + 24} ?: 0
        else -> this % 24
    }

    private fun Int.indexToMinute(): Int = when {
        this < 0 -> (this % -60).takeUnless { it == 0 }?.let { return@let it + 60 } ?: 0
        else -> this % 60
    }

    private fun Int.plusZero(): String = if (this.toString().length == 1) "0$this" else this.toString()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val bool = gestureDetector.onTouchEvent(event)
        event?.takeIf { it.action == MotionEvent.ACTION_UP }?.run {
            when {
                x.isHourPoint() -> if (hourFlingDirection == Direction.NONE && hourScrollDirection != Direction.NONE) getHourNearestOrigin()
                x.isMinutePoint() -> if (minuteFlingDirection == Direction.NONE && minuteScrollDirection != Direction.NONE) getMinuteNearestOrigin()
            }
        }
        return bool
    }

    override fun computeScroll() {
        super.computeScroll()
        if (hourOverScroller.isFinished) {
            if (hourFlingDirection != Direction.NONE) getHourNearestOrigin()
        } else {
            if (hourFlingDirection != Direction.NONE && hourOverScroller.forceFinishScroll()) getHourNearestOrigin()
            if (hourOverScroller.computeScrollOffset()) {
                hourOriginY = hourOverScroller.currY
                postInvalidateOnAnimation()
            }
        }
        if (minuteOverScroller.isFinished) {
            if (minuteFlingDirection != Direction.NONE) getMinuteNearestOrigin()
        } else {
            if (minuteFlingDirection != Direction.NONE && minuteOverScroller.forceFinishScroll()) getMinuteNearestOrigin()
            if (minuteOverScroller.computeScrollOffset()) {
                minuteOriginY = minuteOverScroller.currY
                postInvalidateOnAnimation()
            }
        }
    }

    private fun getHourNearestOrigin() {
        val nearestIndex = -(hourOriginY / cellHeight).roundToInt()
        val nearestOrigin = (nearestIndex * cellHeight + hourOriginY).toInt()
        hourOverScroller.forceFinished(true)
        nearestOrigin.takeIf { it != 0 }?.let {
            hourOverScroller.startScroll(1, hourOriginY, 0, -nearestOrigin, 240)
            postInvalidateOnAnimation()
        }
        hourScrollDirection = Direction.NONE
        hourFlingDirection = Direction.NONE
        //logGesture()
    }

    private fun getMinuteNearestOrigin() {
        val nearestIndex = -(minuteOriginY / cellHeight).roundToInt()
        val nearestOrigin = (nearestIndex * cellHeight + minuteOriginY).toInt()
        minuteOverScroller.forceFinished(true)
        nearestOrigin.takeIf { it != 0 }?.let {
            minuteOverScroller.startScroll((cellWidth * 2).toInt() - 1, minuteOriginY, 0, -nearestOrigin, 240)
            postInvalidateOnAnimation()
        }
        minuteScrollDirection = Direction.NONE
        minuteFlingDirection = Direction.NONE
        //logGesture()
    }

    private fun Float.isHourPoint(): Boolean = this > 0f && this < cellWidth
    private fun Float.isMinutePoint(): Boolean = this > cellWidth && this < cellWidth * 2

    fun textColor(color: Int) {
        textPaint.color = color
        postInvalidateOnAnimation()
    }

}