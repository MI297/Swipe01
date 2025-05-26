//背景にふわふわ動く円（ドット）をアニメーション描画する。
package com.example.swipe01

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp

data class DotState(
    val x: Animatable<Float, AnimationVector1D>,
    val y: Animatable<Float, AnimationVector1D>,
    val radius: Float
)

@Composable
fun BackgroundFloatingDots(swipeCount: Int) {
    val scope = rememberCoroutineScope()
    val dotCount = 10   // ドットの数
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // ドットの初期位置と半径をランダムに決定
    val dots = remember {
        List(dotCount) {
            DotState(
                x = Animatable((0..screenWidth.value.toInt()).random().toFloat()),
                y = Animatable((0..screenHeight.value.toInt()).random().toFloat()),
                radius = (60..100).random().toFloat()
            )
        }
    }

    // スワイプ時にドットがふわっと上に浮かぶ
    LaunchedEffect(swipeCount) {
        dots.forEach { dot ->
            scope.launch {
                dot.y.animateTo(dot.y.value - 30f, animationSpec = tween(500))
            }
        }
    }

    // ドットがゆっくり上に動くループ処理
    LaunchedEffect(Unit) {
        while (true) {
            dots.forEach { dot ->
                scope.launch {
                    val nextY = dot.y.value - 1f
                    val nextX = dot.x.value + listOf(-1f, 0f, 1f).random()
                    dot.y.snapTo(if (nextY < 0) screenHeight.value else nextY)
                    dot.x.snapTo(nextX.coerceIn(0f, screenWidth.value))
                }
            }
            delay(30)
        }
    }

    // ドットを円として描画
    Canvas(modifier = Modifier.fillMaxSize()) {
        dots.forEach { dot ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.LightGray.copy(alpha = 0.2f), Color.Cyan.copy(alpha = 0.1f)),
                    center = Offset(dot.x.value.dp.toPx(), dot.y.value.dp.toPx()),
                    radius = dot.radius.dp.toPx()
                ),
                center = Offset(dot.x.value.dp.toPx(), dot.y.value.dp.toPx()),
                radius = dot.radius.dp.toPx()
            )
        }
    }
}
