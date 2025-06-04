// テスト用画面（ミニ演習集）
// 各タブを切り替えて、Composeの基本要素を体験できるようにする
package com.example.swipe01

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------
// TestScreen：タブによって3つのテストUIを切り替えるメイン画面
@Composable
fun TestScreen() {
    val tabTitles = listOf("カウント", "入力", "条件" , "画像")  // タブのタイトルリスト
    var selectedTabIndex by remember { mutableStateOf(0) } // 現在選択中のタブ番号

    Column {
        // 上部のタブバー
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },  // タブクリックで選択変更
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 選択中のタブに応じて表示内容を切り替える
        when (selectedTabIndex) {
            0 -> CounterTest()
            1 -> InputTest()
            2 -> ConditionTest()
            3 -> SwipeBackgroundTest()
        }
    }
}

// ---------------------------------------------
// カウントの練習：ボタンで数値を増やす／リセットする
@Composable
fun CounterTest() {
    var count by remember { mutableStateOf(0) }  // カウント用の状態

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "カウント：$count", fontSize = 24.sp)  // カウント値表示

        Button(onClick = { count++ }) {  // +1するボタン
            Text("増やす")
        }

        Button(
            onClick = { count = 0 },     // カウントを0に戻すボタン
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("リセット")
        }
    }
}

// ---------------------------------------------
// 入力練習：TextFieldに入力した文字をそのまま表示
@Composable
fun InputTest() {
    var name by remember { mutableStateOf("") }  // 入力された文字を保存する状態

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = name,                             // 現在の入力内容
            onValueChange = { name = it },            // 入力変更時に状態更新
            label = { Text("名前を入力") }           // ラベル表示
        )
        Text(
            text = "こんにちは、$name さん！",         // 入力結果の表示
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

// ---------------------------------------------
// 条件分岐の練習：ボタンでメッセージ表示のON/OFF切り替え
@Composable
fun ConditionTest() {
    var showMessage by remember { mutableStateOf(false) } // 表示切替の状態

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { showMessage = !showMessage }) {
            Text("メッセージを${if (showMessage) "隠す" else "表示"}")
        }

        // 状態に応じて表示されるテキスト
        if (showMessage) {
            Text("これは条件付きで表示されます", Modifier.padding(top = 16.dp))
        }
    }
}

// ---------------------------------------------
// 画像表示＋スワイプ操作：スワイプ左右操作で画像の位置を補正
@Composable
fun SwipeBackgroundTest() {
    var offsetX by remember { mutableStateOf(0f) }

    // スワイプ操作検出
    val gestureModifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
                change.consume()
                offsetX += dragAmount  // スワイプ量を加算
            }
        }

    Box(modifier = gestureModifier) {
        Image(
            painter = painterResource(id = R.drawable.sample_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.toInt(), 0) }  // 横方向にずらす
        )
    }
}
