package com.example.swipe01

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import kotlinx.coroutines.CoroutineScope

import androidx.compose.foundation.Image
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource

@Composable
fun SwipeTwistScreenRoot() {
    var swipeCount by remember { mutableStateOf(0) }
    val twistAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    SwipeTwistScreen(
        swipeCount = swipeCount,
        onSwipe = { swipeCount++ },
        onReset = {
            scope.launch {
                twistAnim.animateTo(0f, tween(300))
                swipeCount = 0
            }
        },
        twistAnim = twistAnim,
        scope = scope
    )
}

@Composable
fun SwipeTwistScreen(
    swipeCount: Int,
    onSwipe: () -> Unit,
    onReset: () -> Unit,
    twistAnim: Animatable<Float, *>,
    scope: CoroutineScope
) {
    var accumulatedDrag by remember { mutableStateOf(0f) }
    var showTutorial by remember { mutableStateOf(true) } //チュートリアル判定
    var hasSeenTutorial by rememberSaveable { mutableStateOf(false) }
    //画像フェードアウト処理判定用
    val tutorialAlpha by animateFloatAsState(
        targetValue = if (swipeCount < 5 && (!hasSeenTutorial ) ) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    // チュートリアル終了判定
    LaunchedEffect(swipeCount) {
        if (swipeCount >= 5 ) {
            showTutorial = false
            hasSeenTutorial = true
        }
    }

    val gestureModifier = Modifier.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onHorizontalDrag = { _, dragAmount ->
                accumulatedDrag += dragAmount
            },
            onDragEnd = {
                val dragMagnitude = accumulatedDrag
                accumulatedDrag = 0f

                onSwipe()

                val normalized = (dragMagnitude / 300f).coerceIn(-5f, 5f)

                scope.launch {
                    twistAnim.animateTo(
                        targetValue = twistAnim.value + normalized,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = LinearOutSlowInEasing
                        )
                    )
                }
            }
        )
    }

    Box(
        modifier = gestureModifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        BackgroundFloatingDots(swipeCount)

        // チュートリアル画像を表示
        if (!hasSeenTutorial || tutorialAlpha > 0f) {
            Image(
                painter = painterResource(id = R.drawable.tutorial_swipe),
                contentDescription = "Swipe tutorial",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(tutorialAlpha)
            )
        }

        TwistedLineCanvas(twistAnim.value, swipeCount)

        SwipeCounterUI(
            swipeCount = swipeCount,
            onReset = onReset,
            scope = scope,
            twistAnim = twistAnim
        )
    }
}