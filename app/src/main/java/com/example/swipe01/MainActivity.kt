package com.example.swipe01

// Compose 基本要素
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
// 描画関連（Canvas、Path、Strokeなど）
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
//アニメーション描画
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import kotlinx.coroutines.launch
// ポインター操作（スワイプ検出）
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
// 単位（dp, sp）
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 三角関数
import kotlin.math.sin
import com.example.swipe01.ui.theme.Swipe01Theme
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Swipe01Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SwipeTwistScreen()
                }
            }
        }
    }
}



@Composable
fun SwipeTwistScreen() {
    var swipeCount by remember { mutableStateOf(0) }
    val twistAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(swipeCount) {
        twistAnim.animateTo(
            targetValue = swipeCount / 2f,
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
        )
    }

    val gestureModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                if (dragAmount > 50) {
                    swipeCount = (swipeCount + 1).coerceAtMost(1000)
                }
            }
        }

    Box(
        modifier = gestureModifier.background(Color.White),
        contentAlignment = Alignment.Center
    ) {

        // 背景
        BackgroundFloatingDots(swipeCount = swipeCount)

        // 線描画（Canvas）
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val heightStep = 20f
            val totalSteps = (size.height / heightStep).toInt()
            val centerIndex = totalSteps / 2

            val path = Path()
            for (i in 0 until totalSteps) {
                val startY = i * heightStep
                val endY = (i + 1) * heightStep

                // 中心に近いほど大きく揺れる（0〜1の範囲）
                val distanceFromCenter = (i - centerIndex).toFloat()
                val influence = 1f - (distanceFromCenter * distanceFromCenter) / (centerIndex * centerIndex)
                val clampedInfluence = influence.coerceIn(0f, 1f)

                val offsetX = sin(i + twistAnim.value) * twistAnim.value * clampedInfluence

                if (i == 0) {
                    path.moveTo(centerX + offsetX, startY)
                } else {
                    path.lineTo(centerX + offsetX, endY)
                }
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 8f)
            )
        }

        // カウント表示
        Text(
            text = "カウント: $swipeCount",
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        //  リセットボタン（画面左下）
        Button(
            onClick = {
                swipeCount = 0
                scope.launch {
                    twistAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(width = 1200.dp, height = 360.dp)
        ) {
            Text("リセット", fontSize = 12.sp)
        }
    }
}

@Composable
fun BackgroundFloatingDots(swipeCount: Int) {
    val scope = rememberCoroutineScope()
    val dotCount = 10
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val dots = remember {
        List(dotCount) {
            DotState(
                x = Animatable((0..screenWidth.value.toInt()).random().toFloat()),
                y = Animatable((0..screenHeight.value.toInt()).random().toFloat()),
                radius = (60..100).random().toFloat()
            )
        }
    }

    // スワイプ時にスピードアップ
    LaunchedEffect(swipeCount) {
        dots.forEach { dot ->
            scope.launch {
                dot.y.animateTo(
                    targetValue = dot.y.value - 30f, // 少し浮かび上がる
                    animationSpec = tween(durationMillis = 500)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            dots.forEach { dot ->
                scope.launch {
                    val nextY = dot.y.value - 1f
                    val nextX = dot.x.value + (listOf(-1f, 0f, 1f).random())
                    // 画面上に戻す
                    dot.y.snapTo(if (nextY < 0) screenHeight.value else nextY)
                    dot.x.snapTo(nextX.coerceIn(0f, screenWidth.value))
                }
            }
            delay(30)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        dots.forEach { dot ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.LightGray.copy(alpha = 0.2f), Color.Cyan.copy(alpha = 0.1f)),
                    center = Offset(dot.x.value.dp.toPx(), dot.y.value.dp.toPx()),
                    radius = dot.radius.dp.toPx()
                ),
                center = Offset(dot.x.value.dp.toPx(), dot.y.value.dp.toPx()),
                radius = dot.radius.dp.toPx()
            )
        }
    }
}

data class DotState(
    val x: Animatable<Float, AnimationVector1D>,
    val y: Animatable<Float, AnimationVector1D>,
    val radius: Float
)

