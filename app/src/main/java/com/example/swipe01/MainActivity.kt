//アプリのメインエントリポイント。画面全体の構成と状態管理を行う。
package com.example.swipe01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    var swipeCount by remember { mutableStateOf(0) }    // スワイプの回数を記録する変数
    val twistAnim = remember { Animatable(0f) }     // 線のねじれ具合をアニメーションで表現する変数
    val scope = rememberCoroutineScope()                        // コルーチンスコープ（非同期処理用）

    // スワイプ回数に応じてアニメーションを変化させる
    LaunchedEffect(swipeCount) {
        twistAnim.animateTo(
            targetValue = swipeCount / 2f,
            animationSpec = tween(400)          // アニメーション時間: 400ミリ秒
        )
    }

    // 画面全体の構成を定義（Boxで重ね合わせ）
    Box(
        // スワイプした時にカウントを増やす
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .swipeDetector {
                swipeCount = (swipeCount + 1).coerceAtMost(1000)
            },
        contentAlignment = Alignment.Center
    ) {
        BackgroundFloatingDots(swipeCount)          // 背景の円アニメーション
        TwistedLineCanvas(twistAnim.value)          // 中央のねじれた線
        SwipeCounterUI(swipeCount, { swipeCount = 0 }, scope, twistAnim)    // カウント表示＆リセット
    }
}
