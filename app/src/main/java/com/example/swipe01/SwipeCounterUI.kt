// UIと状態管理の中心。背景・線・UIの統合管理。
package com.example.swipe01

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
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke

// ボタンの色スタイル一覧
val buttonColors = listOf(
    Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan
)

// 背景の色スタイル一覧
val backgroundColors = listOf(
    Color.Transparent, Color(0xFFE0FFFF), Color(0xFFF0FFF0), Color(0xFFFFF8DC), Color(0xFFE6E6FA),
    Color(0xFFFFE4E1), Color(0xFFFAFAD2), Color(0xFFD3D3D3), Color(0xFFB0E0E6), Color(0xFFEEE8AA)
)

// 枠の色スタイル一覧
val flameColors = listOf(
    Color.Transparent, Color(0xFFE0FFFF), Color(0xFFF0FFF0), Color(0xFFFFF8DC), Color(0xFFE6E6FA),
    Color(0xFFFFE4E1), Color(0xFFFAFAD2), Color(0xFFD3D3D3), Color(0xFFB0E0E6), Color(0xFFEEE8AA)
)


@Composable
fun SwipeCounterUI(
    swipeCount: Int,
    onReset: () -> Unit,
    scope: CoroutineScope,
    twistAnim: Animatable<Float, *>,
    buttonStyleIndex: Int,
    flameStyleIndex: Int,
    backgroundStyleIndex: Int
) {

    // 安全に色を取得
    val buttonColor = buttonColors.getOrElse(buttonStyleIndex) { Color.Gray }


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

//枠のCanvas描画関連
@Composable
fun DrawFrameBorderIfNeeded(styleIndex: Int, variants: List<Color>) {
    val flamegroundColor = variants.getOrNull(styleIndex) ?: Color.Transparent
    if (flamegroundColor != Color.Transparent) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = flamegroundColor,
                size = size,
                style = Stroke(width = 40f)
            )
        }
    }
}

//中間背景画像表示
@Composable
fun DrawBackgroundLayer(styleIndex: Int, variants: List<Color>) {
    val backgroundColor = variants.getOrNull(styleIndex) ?: Color.Transparent
    if (backgroundColor != Color.Transparent) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                color = backgroundColor,
                size = size
            )
        }
    }
}

//切替・背景ボタンUI
@Composable
fun StyleSwitchButtons(
    onButtonStyleChange: () -> Unit,
    onFrameStyleChange: () -> Unit,
    onBackgroundStyleChange: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onButtonStyleChange) {
                Text("ボタン切替")
            }
            Button(onClick = onFrameStyleChange) {
                Text("枠切替")
            }
            Button(onClick = onBackgroundStyleChange) {
                Text("背景切替")
            }
        }
    }
}




