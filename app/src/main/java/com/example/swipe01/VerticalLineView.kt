package com.example.swipe01

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.util.Log

class VerticalLineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = 0xFF000000.toInt()
        strokeWidth = 20f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("VerticalLineView", "onDraw called")
        val centerX = width / 2f
        canvas.drawLine(centerX, 0f, centerX, height.toFloat(), paint)
    }
}
