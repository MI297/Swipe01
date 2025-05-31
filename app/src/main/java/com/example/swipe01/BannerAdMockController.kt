package com.example.swipe01


import androidx.compose.animation.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.animation.core.tween

@Composable
fun BannerAdMockController(
    swipeTriggered: Boolean
) {
    val showBanner = remember { mutableStateOf(false) }
    val elapsedTime = remember { mutableIntStateOf(0) }
    val isBlinking = remember { mutableStateOf(false) }

    // タイマー開始（最初のスワイプ後に1回だけ）
    LaunchedEffect(swipeTriggered) {
        if (swipeTriggered) {
            for (i in 1..10) {
                delay(1000L)
                elapsedTime.value = i
            }
            showBanner.value = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 上部のデバッグ表示
        Column(modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) {
            Text("経過秒: ${elapsedTime.value}")
            Text("バナー表示: ${showBanner.value}")
        }

        // 下部のバナー広告
        if (showBanner.value) {
            val blinkColor = remember { Animatable(Color(0xFFEEEEEE)) }

            // タップ時に青く点滅
            LaunchedEffect(isBlinking.value) {
                if (isBlinking.value) {
                    blinkColor.animateTo(Color.Blue, animationSpec = tween(200))
                    blinkColor.animateTo(Color(0xFFEEEEEE), animationSpec = tween(200))
                    isBlinking.value = false
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 60.dp) // リセットボタン等と被らないように
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(blinkColor.value)
                    .clickable { isBlinking.value = true },
                contentAlignment = Alignment.Center
            ) {
                Text("📢 バナー広告（テスト用）", color = Color.Black, fontSize = 14.sp)
            }
        }
    }
}
