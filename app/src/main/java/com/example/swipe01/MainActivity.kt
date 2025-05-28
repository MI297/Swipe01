//
package com.example.swipe01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.swipe01.ui.theme.Swipe01Theme
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Swipe01Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Log.d("MainActivity", "Main started")
                    SwipeTwistScreenRoot()  // 状態管理付きの新しいComposable
                }
            }
        }
    }
}


