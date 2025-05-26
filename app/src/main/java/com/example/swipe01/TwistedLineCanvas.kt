package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.*

@Composable
fun TwistedLineCanvas(twistValue: Float, swipeCount: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val heightStep = 20f
        val totalSteps = (size.height / heightStep).toInt()
        val centerIndex = totalSteps / 2
        val chaosFactor = (swipeCount / 10f).coerceAtMost(20f)

        val path = Path()
        for (i in 0 until totalSteps) {
            val startY = i * heightStep
            val endY = (i + 1) * heightStep
            val distance = (i - centerIndex).toFloat()
            val influence = 1f - (distance * distance) / (centerIndex * centerIndex)
            val clampedInfluence = influence.coerceIn(0f, 1f)

            if (swipeCount == 0) {
                for (i in 0 until totalSteps) {
                    val y = i * heightStep
                    if (i == 0) path.moveTo(centerX, y)
                    else path.lineTo(centerX, y)
                }
                drawPath(path, color = Color.Black, style = Stroke(width = 8f))
                return@Canvas
            }

            val twistOffset = sin(i + twistValue * 5f) * twistValue * clampedInfluence * chaosFactor
            val loopOffset = if ( abs(distance) < 8f) {
                sin(i * 2f) * 8f * (1f - abs(distance) / 8f)
            } else 0f

            val offsetX = twistOffset + loopOffset

            if (i == 0) path.moveTo(centerX + offsetX, startY)
            else path.lineTo(centerX + offsetX, endY)
        }

        drawPath(path = path, color = Color.Black, style = Stroke(width = 8f))
    }
}
