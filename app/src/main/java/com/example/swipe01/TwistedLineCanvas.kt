package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.sin

@Composable
fun TwistedLineCanvas(twistValue: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val heightStep = 20f
        val totalSteps = (size.height / heightStep).toInt()
        val centerIndex = totalSteps / 2

        val path = Path()
        for (i in 0 until totalSteps) {
            val startY = i * heightStep
            val endY = (i + 1) * heightStep
            val distanceFromCenter = (i - centerIndex).toFloat()
            val influence = 1f - (distanceFromCenter * distanceFromCenter) / (centerIndex * centerIndex)
            val clampedInfluence = influence.coerceIn(0f, 1f)
            val offsetX = sin(i + twistValue) * twistValue * clampedInfluence

            if (i == 0) {
                path.moveTo(centerX + offsetX, startY)
            } else {
                path.lineTo(centerX + offsetX, endY)
            }
        }

        drawPath(path = path, color = Color.Black, style = Stroke(width = 8f))
    }
}
