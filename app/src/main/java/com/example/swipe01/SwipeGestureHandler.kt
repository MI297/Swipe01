//スワイプ検出の処理。横方向のスワイプに応じてカウントを増やす。
package com.example.swipe01

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.swipeDetector(onSwipe: () -> Unit): Modifier {
    return this.pointerInput(Unit) {
        detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount > 50) {      // 右方向に強くスワイプした時
                onSwipe()               // 呼び出し元で定義した処理を実行
            }
        }
    }
}
