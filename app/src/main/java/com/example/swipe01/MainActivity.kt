package com.example.swipe01

// Compose 基本要素
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// 描画関連（Canvas、Path、Strokeなど）
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
//アニメーション描画
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import kotlinx.coroutines.launch
// ポインター操作（スワイプ検出）
import androidx.compose.ui.input.pointer.pointerInput
// 単位（dp, sp）
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 三角関数
import kotlin.math.sin
import com.example.swipe01.ui.theme.Swipe01Theme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Swipe01Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SwipeTwistScreen()
                }
            }
        }
    }
}

@Composable
fun SwipeTwistScreen() {
    var swipeCount by remember { mutableStateOf(0) }
    val twistAnim = remember { Animatable(0f) }

    LaunchedEffect(swipeCount) {
        // アニメーションで値を滑らかに変える
        twistAnim.animateTo(
            targetValue = swipeCount / 2f, // ねじれ具合
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
        )
    }

    val gestureModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { _, dragAmount ->
                if (dragAmount > 50) {
                    swipeCount = (swipeCount + 1).coerceAtMost(100)
                }
            }
        }

    Box(
        modifier = gestureModifier.background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val heightStep = 40f
            val totalSteps = (size.height / heightStep).toInt()

            val path = Path()
            for (i in 0 until totalSteps) {
                val startY = i * heightStep
                val endY = (i + 1) * heightStep
                val offsetX = sin(i + twistAnim.value) * twistAnim.value

                if (i == 0) {
                    path.moveTo(centerX + offsetX, startY)
                } else {
                    path.lineTo(centerX + offsetX, endY)
                }
            }

            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 8f)
            )
        }

        Text(
            text = "カウント: $swipeCount",
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }
}
