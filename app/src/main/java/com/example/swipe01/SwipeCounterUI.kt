package com.example.swipe01

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween

@Composable
fun SwipeCounterUI(
    swipeCount: Int,
    onReset: () -> Unit,
    scope: CoroutineScope,
    twistAnim: Animatable<Float, *>,
    buttonStyleIndex: Int,
    backgroundStyleIndex: Int
) {
    // ボタンの色スタイル一覧
    val buttonColors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan,
        Color.Yellow, Color.Gray, Color.Black, Color.DarkGray, Color.LightGray
    )

    // 背景の色スタイル一覧
    val backgroundColors = listOf(
        Color(0xFFFFF0F0), Color(0xFFE0FFFF), Color(0xFFF0FFF0), Color(0xFFFFF8DC), Color(0xFFE6E6FA),
        Color(0xFFFFE4E1), Color(0xFFFAFAD2), Color(0xFFD3D3D3), Color(0xFFB0E0E6), Color(0xFFEEE8AA)
    )

    // 安全に色を取得
    val buttonColor = buttonColors.getOrElse(buttonStyleIndex) { Color.Gray }
    val backgroundColor = backgroundColors.getOrElse(backgroundStyleIndex) { Color.White }

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .wrapContentSize()  // ← サイズに合わせる
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // カウント表示
            Text(
                text = "カウント: $swipeCount",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // リセットボタン
            Button(
                onClick = {
                    onReset()
                    scope.launch {
                        twistAnim.animateTo(0f, animationSpec = tween(300))
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                modifier = Modifier
                    .align(Alignment.Start)
                    .size(width = 120.dp, height = 36.dp)
            ) {
                Text("リセット", fontSize = 12.sp)
            }
        }
    }
}







