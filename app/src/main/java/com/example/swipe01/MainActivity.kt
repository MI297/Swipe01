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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin
import kotlin.math.abs
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.swipe01.ui.theme.Swipe01Theme

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
    var accumulatedDrag by remember { mutableStateOf(0f) }

    val gestureModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures(
                onHorizontalDrag = { _, dragAmount ->
                    accumulatedDrag += dragAmount
                },
                onDragEnd = {
                    val dragMagnitude = accumulatedDrag
                    accumulatedDrag = 0f
                    swipeCount = (swipeCount + 1).coerceAtMost(1000)
                    val normalized = (dragMagnitude / 300f).coerceIn(-5f, 5f)
                    scope.launch {
                        twistAnim.animateTo(
                            targetValue = twistAnim.value + normalized,
                            animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
                        )
                    }
                }
            )
        }

    Box(
        modifier = gestureModifier.background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BackgroundFloatingDots(swipeCount = swipeCount)

        Canvas(modifier = Modifier.fillMaxSize()) {
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
                    if (i == 0) path.moveTo(centerX, startY)
                    else path.lineTo(centerX, endY)
                    continue
                }

                val twistOffset = sin(i + twistAnim.value * 5f) * twistAnim.value * clampedInfluence * chaosFactor
                val randomJitter = 0f
                val loopOffset = if (abs(distance) < 5f) {
                    sin(i * 4f) * 20f * (1f - abs(distance) / 5f)
                } else 0f
                val offsetX = twistOffset + randomJitter + loopOffset

                if (i == 0) path.moveTo(centerX + offsetX, startY)
                else path.lineTo(centerX + offsetX, endY)
            }

            drawPath(path = path, color = Color.Black, style = Stroke(width = 8f))
        }

        Text(
            text = "カウント: $swipeCount",
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp)
        )

        Button(
            onClick = {
                scope.launch {
                    twistAnim.animateTo(0f, tween(300))
                    swipeCount = 0
                }
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
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

    LaunchedEffect(swipeCount) {
        dots.forEach { dot ->
            scope.launch {
                dot.y.animateTo(
                    targetValue = dot.y.value - 30f,
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
                    if (nextY < -dot.radius) {
                        dot.y.snapTo(screenHeight.value)
                        dot.x.snapTo((0..screenWidth.value.toInt()).random().toFloat())
                    } else {
                        dot.y.snapTo(nextY)
                        dot.x.snapTo(nextX.coerceIn(0f, screenWidth.value))
                    }
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
