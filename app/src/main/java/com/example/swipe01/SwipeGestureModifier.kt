package com.example.swipe01

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInteropFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.gestures.detectDragGestures
import kotlin.math.abs

fun Modifier.swipeGestureHandler(
    isCutting: Boolean,
    isCutCompleted: Boolean,
    scope: CoroutineScope,
    onSwipeTwist: () -> Unit,
    onCutStart: () -> Unit,
    onCutFinish: () -> Unit
): Modifier = this.pointerInput(isCutting, isCutCompleted) {
    var accumulatedX = 0f
    var accumulatedY = 0f

    detectDragGestures(
        onDrag = { _, dragAmount ->
            accumulatedX += dragAmount.x
            accumulatedY += dragAmount.y
        },
        onDragEnd = {
            if (isCutting || isCutCompleted) return@detectDragGestures

            if (abs(accumulatedY) > abs(accumulatedX) && accumulatedY > 100f) {
                onCutStart()
                scope.launch {
                    delay(1500)
                    onCutFinish()
                }
            } else if (accumulatedX > 100f) {
                onSwipeTwist()
            }

            accumulatedX = 0f
            accumulatedY = 0f
        }
    )
}
