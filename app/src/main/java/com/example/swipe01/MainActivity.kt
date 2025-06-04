// アプリ起動時のエントリポイント
//SwipeTwistScreen を呼び出す。
package com.example.swipe01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.swipe01.ui.theme.Swipe01Theme

private const val TEST_SCREEN = true //テストモードの切り替え

class MainActivity : ComponentActivity() {
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundPoolManager.initialize(this)  // サウンドマネージャー呼び出し
        setContent {
            Swipe01Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if(TEST_SCREEN) {
                        TestScreen()    //テスト用モード
                    } else {
                        SwipeTwistScreenRoot()  // メイン処理
                    }
                }
            }
        }
    }
}


