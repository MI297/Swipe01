//カウント表示とリセットボタンのUIを担当する。
package com.example.swipe01

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    twistAnim: Animatable<Float, *>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 上にカウント表示
        Text(
            text = "カウント: $swipeCount",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp
        )

        // 真ん中のスペースを空ける
        Spacer(modifier = Modifier.weight(1f))

        // 下にリセットボタン
        Button(
            onClick = {
                onReset()
                //重複している処理なので削除　（SwipeTwistScreen.kt）
//                scope.launch {
//                    twistAnim.animateTo(0f, animationSpec = tween(300))
//                }
            },
            modifier = Modifier
                .align(Alignment.Start)
                .size(width = 120.dp, height = 36.dp)
        ) {
            Text("リセット", fontSize = 12.sp)
        }
    }
}
