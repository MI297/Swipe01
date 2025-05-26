package com.example.swipe01

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing

@Composable
fun SwipeTwistScreen() {
    var swipeCount by remember { mutableStateOf(0) }
    val twistAnim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var accumulatedDrag by remember { mutableStateOf(0f) }

    // スワイプ処理
    val gestureModifier = Modifier.pointerInput(Unit) {
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
            onReset = {
                scope.launch {
                    // まずアニメーションでねじれを元に戻す
                    twistAnim.animateTo(0f, tween(300))
                    // 完了後にカウントもリセット（描画条件が正しく働く）
                    swipeCount = 0
                }
            },
            scope = scope,
            twistAnim = twistAnim
        )
    }
}