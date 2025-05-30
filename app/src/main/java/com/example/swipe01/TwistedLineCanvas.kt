package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
fun TwistedLineCanvasWithCapture(
    twistValue: Float,
    swipeCount: Int,
    onCapturePoints: (List<Offset>) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val points = generateTwistedLinePoints(twistValue, width, height)
        onCapturePoints(points)

        for (i in 1 until points.size) {
            drawLine(
                color = getSwipeColor(swipeCount),
                start = points[i - 1],
                end = points[i],
                strokeWidth = 8f
            )
        }
    }
}
