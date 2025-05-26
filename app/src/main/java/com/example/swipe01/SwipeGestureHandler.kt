package com.example.swipe01

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.swipeDetector(onSwipe: () -> Unit): Modifier {
    return this.pointerInput(Unit) {
        detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount > 50) {
                onSwipe()
            }
        }
    }
}
