package com.example.swipe01

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class RopeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    init {
        isClickable = true
        isFocusable = true
    }

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 8f
        isAntiAlias = true
    }

    private val startX = 500f
    private val startY = 200f
    private var endX = startX
    private var endY = startY + 300f
    private var isCut = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isCut) {
            canvas.drawLine(startX, startY, endX, endY, paint)
        } else {
            paint.color = Color.RED
            paint.textSize = 50f
            canvas.drawText("プツン…切れた！", 300f, 500f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isCut) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                val distance = calcDistance(startX, startY, endX, endY)
                if (distance > 150) isCut = true
                invalidate()
            }
        }
        return true
    }

    fun resetRope() {
        isCut = false
        endX = startX
        endY = startY + 300f
        invalidate()
    }

    private fun calcDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }
}
