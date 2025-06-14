// UIと状態管理の中心。背景・線・UIの統合管理。
package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun SwipeTwistScreenRoot() {
    var swipeCount by remember { mutableIntStateOf(0) }         //スワイプ回数
    val twistAnim = remember { Animatable(0f) }             //
    val scope = rememberCoroutineScope()

    var isCutting by remember { mutableStateOf(false) }         //cutモード状態管理
    var isCutCompleted by remember { mutableStateOf(false) }    //cutアニメーション終了
    var showCutMessage by remember { mutableStateOf(false) }

    var cachedLinePoints by remember { mutableStateOf<List<Offset>?>(null) }

    var hasSeenTutorial by rememberSaveable { mutableStateOf(false) }

    // チュートリアルモード切替条件　スワイプ5回で終了
    val tutorialAlpha by animateFloatAsState(
        targetValue = if (swipeCount < 5 && !hasSeenTutorial && !isCutting) 1f else 0f,
        animationSpec = tween(1000)
    )
    LaunchedEffect(swipeCount) {
        if (swipeCount >= 5) hasSeenTutorial = true
    }

    // cutモード
    val cutAlpha = remember { Animatable(1f) }
    val cutAnimY = remember { Animatable(0f) }
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
            SoundPoolManager.playCutSound() //効果音再生
            cutAlpha.snapTo(1f)
            cutAnimY.snapTo(0f)
            launch { cutAlpha.animateTo(0f, tween(1500)) }
            launch { cutAnimY.animateTo(200f, tween(1500)) }
        }
    }

    LaunchedEffect(twistAnim.value) {
        if (!isCutting && !isCutCompleted) {
            SoundPoolManager.playTwistSound()  //効果音再生
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
    var flameStyleIndex by remember { mutableIntStateOf(0) }

    Box(modifier = gestureModifier.fillMaxSize()) {

        // 1=>背景水玉（最背面） 2=>test用追加背景スタイル
        when (backgroundStyleIndex) {
            3 -> TestBackground3()
            2 -> BackgroundTriangles()
            1 -> BackgroundSquares()  // index == 2 のときだけ別背景に
            else -> BackgroundFloatingDots(swipeCount)  // 通常は水玉背景
        }

        // 枠描画
        DrawFrameBorderIfNeeded(
            styleIndex = flameStyleIndex,
            variants = flameColors
        )

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
                flameStyleIndex = flameStyleIndex,
                backgroundStyleIndex = backgroundStyleIndex
            )

            StyleSwitchButtons(
                onButtonStyleChange = { buttonStyleIndex = (buttonStyleIndex + 1) % buttonColors.size },
                onFrameStyleChange = { flameStyleIndex = (flameStyleIndex + 1) % flameColors.size },
                onBackgroundStyleChange = { backgroundStyleIndex = (backgroundStyleIndex + 1) % BackgroundStyles.MAX }

            )

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
