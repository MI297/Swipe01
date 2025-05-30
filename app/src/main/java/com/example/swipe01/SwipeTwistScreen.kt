package com.example.swipe01

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.ui.platform.LocalContext
import com.example.swipe01.SoundPoolManager

@Composable
fun SwipeTwistScreenRoot() {
    var swipeCount by remember { mutableIntStateOf(0) }
    val twistAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var isCutting by remember { mutableStateOf(false) }
    var isCutCompleted by remember { mutableStateOf(false) }
    var showCutMessage by remember { mutableStateOf(false) }

    var cachedLinePoints by remember { mutableStateOf<List<Offset>?>(null) }

    var hasSeenTutorial by rememberSaveable { mutableStateOf(false) }
    val tutorialAlpha by animateFloatAsState(
        targetValue = if (swipeCount < 5 && !hasSeenTutorial && !isCutting) 1f else 0f,
        animationSpec = tween(1000)
    )
    LaunchedEffect(swipeCount) {
        if (swipeCount >= 5) hasSeenTutorial = true
    }

    val cutAlpha = remember { Animatable(1f) }
    val cutAnimY = remember { Animatable(0f) }

    // Sound
    val context = LocalContext.current


    LaunchedEffect(isCutting) {
        if (isCutting) {
            // Capture current line shape immediately
            if (cachedLinePoints == null) {
                cachedLinePoints = generateTwistedLinePoints(
                    twistAnim.value,
                    context.resources.displayMetrics.widthPixels.toFloat(),
                    context.resources.displayMetrics.heightPixels.toFloat()
                )
            }
            //soundPoolManager.playCutSound()
            cutAlpha.snapTo(1f)
            cutAnimY.snapTo(0f)
            launch { cutAlpha.animateTo(0f, tween(1500)) }
            launch { cutAnimY.animateTo(200f, tween(1500)) }
        }
    }

    LaunchedEffect(twistAnim.value) {
        if (!isCutting && !isCutCompleted) {
            //soundPoolManager.playTwistSound()
        }
    }

    LaunchedEffect(isCutCompleted) {
        if (isCutCompleted) {
            delay(1500)
            showCutMessage = true
        }
    }

    val gestureModifier = Modifier.swipeGestureHandler(
        isCutting = isCutting,
        isCutCompleted = isCutCompleted,
        scope = scope,
        onSwipeTwist = {
            swipeCount++
            scope.launch {
                twistAnim.animateTo(
                    twistAnim.value + 1f,
                    animationSpec = tween(500)
                )
            }
        },
        onCutStart = {
            isCutting = true
        },
        onCutFinish = {
            isCutting = false
            isCutCompleted = true
        }
    )

    var buttonStyleIndex by remember { mutableIntStateOf(0) }
    var backgroundStyleIndex by remember { mutableIntStateOf(0) }

    Box(modifier = gestureModifier.fillMaxSize()) {

        // 背景水玉（最背面）
        BackgroundFloatingDots(swipeCount)

        // 線または切断線
        if (isCutting || isCutCompleted) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val midY = size.height / 2f
                val points = cachedLinePoints ?: return@Canvas
                val upper = points.filter { it.y <= midY }
                val lower = points.filter { it.y > midY }

                for (i in 1 until upper.size) {
                    drawLine(
                        color = Color.Red.copy(alpha = cutAlpha.value),
                        start = upper[i - 1],
                        end = upper[i],
                        strokeWidth = 8f
                    )
                }

                for (i in 1 until lower.size) {
                    val start = lower[i - 1] + Offset(0f, cutAnimY.value)
                    val end = lower[i] + Offset(0f, cutAnimY.value)
                    drawLine(
                        color = Color.Red.copy(alpha = cutAlpha.value),
                        start = start,
                        end = end,
                        strokeWidth = 8f
                    )
                }
            }

            if (showCutMessage) {
                Text("プツン…切れた！", fontSize = 24.sp, color = Color.Red)
            }
        } else {
            TwistedLineCanvasWithCapture(
                twistValue = twistAnim.value,
                swipeCount = swipeCount,
                onCapturePoints = { captured -> cachedLinePoints = captured }
            )
        }

        // チュートリアル画像（中間）
        if (tutorialAlpha > 0f) {
            Image(
                painter = painterResource(id = R.drawable.tutorial_swipe),
                contentDescription = "Swipe tutorial",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(tutorialAlpha)
            )
        }

        // 枠（線の上・UIの下）
        if (backgroundStyleIndex == 1) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    color = Color.Cyan,
                    size = size,
                    style = Stroke(width = 16f)
                )
            }
        }

        // UI（最前面）
        if (!isCutting) {
            SwipeCounterUI(
                swipeCount = swipeCount,
                onReset = {
                    scope.launch {
                        twistAnim.animateTo(0f, tween(800, easing = LinearOutSlowInEasing))
                    }
                    swipeCount = 0
                    isCutting = false
                    isCutCompleted = false
                    showCutMessage = false
                    cachedLinePoints = null
                    scope.launch {
                        cutAlpha.snapTo(1f)
                        cutAnimY.snapTo(0f)
                    }
                },
                scope = scope,
                twistAnim = twistAnim,
                buttonStyleIndex = buttonStyleIndex,
                backgroundStyleIndex = backgroundStyleIndex
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { buttonStyleIndex = (buttonStyleIndex + 1) % 3 }) {
                    Text("ボタン切替")
                }
                Button(onClick = { backgroundStyleIndex = (backgroundStyleIndex + 1) % 2 }) {
                    Text("背景切替")
                }
            }
        }
    }
}

fun generateTwistedLinePoints(twistValue: Float, canvasWidth: Float, canvasHeight: Float): List<Offset> {
    val result = mutableListOf<Offset>()
    val heightStep = 20f
    val totalSteps = (canvasHeight / heightStep).toInt()
    val centerX = canvasWidth / 2f
    val centerIndex = totalSteps / 2
    val chaosFactor = 2f

    for (i in 0 until totalSteps) {
        val y = i * heightStep
        val distance = (i - centerIndex).toFloat()
        val influence = 1f - (distance * distance) / (centerIndex * centerIndex)
        val clampedInfluence = influence.coerceIn(0f, 1f)
        val twistOffset = kotlin.math.sin(i + twistValue * 5f) * twistValue * clampedInfluence * chaosFactor
        result.add(Offset(centerX + twistOffset, y))
    }
    return result
}
