//テストプログラム（ミニ演習）
package com.example.swipe01

import androidx.compose.runtime.Composable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp

// TestScreenメイン関数
// MainActvity.kt　上で呼び出されている

@Composable
fun TestScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("📘 Test Screen", fontSize = 24.sp)

        CounterTest()
        InputTest()
        ConditionTest()
    }
}

//
//
@Composable
fun CounterTest() {
    var count by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "カウント：$count", fontSize = 24.sp)
        Button(onClick = { count++ }) {
            Text("増やす")
        }
        Button(onClick = { count = 0 }, Modifier.padding(top = 8.dp)) {
            Text("リセット")
        }
    }
}

@Composable
fun InputTest() {
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("名前を入力") }
        )
        Text(text = "こんにちは、$name さん！", fontSize = 20.sp, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun ConditionTest() {
    var showMessage by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { showMessage = !showMessage }) {
            Text("メッセージを${if (showMessage) "隠す" else "表示"}")
        }

        if (showMessage) {
            Text("これは条件付きで表示されます", Modifier.padding(top = 16.dp))
        }
    }
}
