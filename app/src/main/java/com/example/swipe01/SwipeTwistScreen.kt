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
import kotlinx.coroutines.CoroutineScope


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
        TwistedLineCanvas(twistAnim.value, swipeCount)
        SwipeCounterUI(
            swipeCount = swipeCount,
            onReset = onReset,
            scope = scope,
            twistAnim = twistAnim
        )
    }
}