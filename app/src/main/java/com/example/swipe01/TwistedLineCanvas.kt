package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.*


// 10回ごとに変わる色のリスト
val colorStops = listOf(
    Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta
)

fun getSwipeColor(swipeCount: Int): Color {
    val stage = swipeCount / 10          // 現在の段階（何色目か）
    val progress = (swipeCount % 10) / 10f // 現段階での進行度（0.0～1.0）

    // ループするようにインデックスを制限
    val maxIndex = colorStops.size - 1
    val safeStage = stage % maxIndex

    val startColor = colorStops[safeStage]
    val endColor = colorStops[(safeStage + 1) % colorStops.size]

    return lerp(startColor, endColor, progress)
}



@Composable
fun TwistedLineCanvas(twistValue: Float, swipeCount: Int, modifier: Modifier = Modifier) {
    val swipeColor = getSwipeColor(swipeCount)

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
                for (j in 0 until totalSteps) {
                    val y = j * heightStep
                    if (j == 0) path.moveTo(centerX, y)
                    else path.lineTo(centerX, y)
                }
                drawPath(path, color = swipeColor, style = Stroke(width = 8f))
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

        drawPath(path = path, color = swipeColor, style = Stroke(width = 8f))
    }
}
