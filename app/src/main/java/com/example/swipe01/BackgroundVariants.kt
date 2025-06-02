//背景パターン追加テスト
package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

//パターン数
object BackgroundStyles {
    const val MAX = 3
    val styleNames = listOf(
        "水玉", "落下四角", "回転三角"
    )
}

// 状態クラス
data class BoxState(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float
)

//四角形が降ってくるエフェクト
@Composable
fun BackgroundSquares(swipeCount: Int) {
    val boxCount = 20
    val boxStates = remember { mutableStateListOf<BoxState>() }
    val redrawTrigger = remember { mutableIntStateOf(0) }

    // 四角形の初期化
    LaunchedEffect(Unit) {
        boxStates.clear()
        repeat(boxCount) {
            boxStates += BoxState(
                x = Random.nextFloat() * 1080f,
                y = -100f + Random.nextFloat() * -400f,
                size = 100f + Random.nextFloat() * 50f,
                speed = 1f + Random.nextFloat() * 1.5f
            )
        }
    }

    // 四角形の位置を常に更新
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            boxStates.forEach { it.y += it.speed }
            boxStates.removeAll { it.y > 2000f }
            while (boxStates.size < boxCount) {
                boxStates += BoxState(
                    x = Random.nextFloat() * 1080f,
                    y = -100f + Random.nextFloat() * -400f,
                    size = 100f + Random.nextFloat() * 50f,
                    speed = 1f + Random.nextFloat() * 1.5f
                )
            }
            redrawTrigger.value++  // Composeに明示的な再描画を促す
        }
    }


// 表示内容：Canvas + Debugテキスト
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            boxStates.forEach { box ->
                val brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF5E1), Color(0xFFFFDAB9)),
                    startY = box.y,
                    endY = box.y + box.size
                )

                drawRect(
                    brush = brush,
                    topLeft = Offset(box.x, box.y),
                    size = Size(box.size, box.size)
                )

                drawRect(
                    color = Color.White,
                    topLeft = Offset(box.x, box.y),
                    size = Size(box.size, box.size),
                    style = Stroke(width = 2f)
                )
            }
        }

        // Debug表示：画面左上に再描画カウンター表示

        Text(
            text = "Redraw: ${redrawTrigger.value}",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.7f))
                .padding(4.dp)
        )


    }
}


data class TriangleState(
    var x: Float,
    var y: Float,
    val size: Float,
    val speed: Float,
    var rotation: Float,
    val rotationSpeed: Float
)

//三角形が漂うエフェクト
@Composable
fun BackgroundTriangles(swipeCount: Int) {
    val triangleCount = 20
    val triangleStates = remember { mutableStateListOf<TriangleState>() }
    val redrawTrigger = remember { mutableIntStateOf(0) }

    // 初期化
    LaunchedEffect(Unit) {
        triangleStates.clear()
        repeat(triangleCount) {
            val size = 60f + Random.nextFloat() * 60f
            triangleStates += TriangleState(
                x = Random.nextFloat() * -500f,
                y = Random.nextFloat() * 2000f,
                size = size,
                speed = 0.5f + Random.nextFloat() * 1.5f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = 100f / size
            )
        }
    }

    // 常に動かし続ける
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            triangleStates.forEach {
                it.x += it.speed
                it.rotation += it.rotationSpeed
                if (it.rotation >= 360f) it.rotation -= 360f
            }
            triangleStates.removeAll { it.x > 2000f }
            while (triangleStates.size < triangleCount) {
                val size = 60f + Random.nextFloat() * 60f
                triangleStates += TriangleState(
                    x = Random.nextFloat() * -300f,
                    y = Random.nextFloat() * 2000f,
                    size = size,
                    speed = 0.5f + Random.nextFloat() * 1.5f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = 100f / size
                )
            }
            redrawTrigger.value++
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            triangleStates.forEach { tri ->
                val brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFE5B4), Color(0xFFFFA500)),
                    startX = tri.x,
                    endX = tri.x + tri.size
                )

                val center = Offset(tri.x + tri.size / 2f, tri.y + tri.size / 2f)

                rotate(degrees = tri.rotation, pivot = center) {
                    val path = Path().apply {
                        moveTo(center.x, center.y - tri.size / 2f)  // 上
                        lineTo(center.x - tri.size / 2f, center.y + tri.size / 2f) // 左下
                        lineTo(center.x + tri.size / 2f, center.y + tri.size / 2f) // 右下
                        close()
                    }

                    drawPath(path = path, brush = brush)
                    drawPath(path = path, color = Color.White, style = Stroke(width = 2f))
                }
            }
        }

        // Debug表示

        Text(
            text = "Redraw: ${redrawTrigger.value}",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.7f))
                .padding(4.dp)
        )

    }
}


//仮背景エフェクトパターン３
@Composable
fun TestBackground3(swipeCount: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // 仮演出
        drawRect(Color(0xAFAAFFFA))
    }
}
