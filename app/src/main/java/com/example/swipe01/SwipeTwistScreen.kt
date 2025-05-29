package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun SwipeTwistScreenRoot() {
    var swipeCount by remember { mutableStateOf(0) }
    val twistAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var isCutting by remember { mutableStateOf(false) }
    var isCutCompleted by remember { mutableStateOf(false) }

    // 線のスナップショット保持用（切断時）
    var cachedLinePoints by remember { mutableStateOf<List<Offset>?>(null) }

    // チュートリアル制御（初回のみ表示）
    var hasSeenTutorial by rememberSaveable { mutableStateOf(false) }
    val tutorialAlpha by animateFloatAsState(
        targetValue = if (swipeCount < 5 && !hasSeenTutorial && !isCutting) 1f else 0f,
        animationSpec = tween(1000)
    )
    LaunchedEffect(swipeCount) {
        if (swipeCount >= 5) hasSeenTutorial = true
    }

    // 切断アニメーション：線のフェードと落下用
    val cutAlpha = remember { Animatable(1f) }
    val cutAnimY = remember { Animatable(0f) }
    LaunchedEffect(isCutting) {
        if (isCutting) {
            cutAlpha.snapTo(1f)
            cutAnimY.snapTo(0f)
            launch { cutAlpha.animateTo(0f, tween(1500)) }
            launch { cutAnimY.animateTo(200f, tween(1500)) }
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
            // cachedLinePoints は Canvas 側で初回に生成する
        },
        onCutFinish = {
            isCutting = false
            isCutCompleted = true
        }
    )

    Box(
        modifier = gestureModifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BackgroundFloatingDots(swipeCount)

        if (tutorialAlpha > 0f) {
            Image(
                painter = painterResource(id = R.drawable.tutorial_swipe),
                contentDescription = "Swipe tutorial",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(tutorialAlpha)
            )
        }

        if (isCutting || isCutCompleted) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val midY = size.height / 2f

                if (cachedLinePoints == null) {
                    cachedLinePoints = generateTwistedLinePoints(
                        twistAnim.value,
                        size.width,
                        size.height
                    )
                }

                val points = cachedLinePoints ?: return@Canvas
                val upper = points.filter { it.y <= midY }
                val lower = points.filter { it.y > midY }

                // 上半分（固定）
                for (i in 1 until upper.size) {
                    drawLine(
                        color = Color.Red.copy(alpha = cutAlpha.value),
                        start = upper[i - 1],
                        end = upper[i],
                        strokeWidth = 8f
                    )
                }

                // 下半分（落下）
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
        } else {
            TwistedLineCanvas(twistAnim.value, swipeCount)
        }

        SwipeCounterUI(
            swipeCount = swipeCount,
            onReset = {
                scope.launch {
                    twistAnim.animateTo(0f, tween(800, easing = LinearOutSlowInEasing))
                }
                swipeCount = 0
                isCutting = false
                isCutCompleted = false
                cachedLinePoints = null
            },
            scope = scope,
            twistAnim = twistAnim
        )
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
