package com.example.swipe01.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

//ダークカラーテーマ
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)
//白基調のカスタムテーマ
private val CustomLightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),        // 青（ボタンなどのアクセント）
    onPrimary = Color.White,            // プライマリの上のテキスト色

    secondary = Color(0xFF90CAF9),      // 補助色（薄めの青）
    onSecondary = Color.Black,

    background = Color(0xFFFFFFFF),     // 背景：真っ白
    onBackground = Color(0xFF000000),   // 背景上の文字：黒

    surface = Color(0xFFF5F5F5),        // カードやボタンの背景
    onSurface = Color(0xFF000000),      // その上のテキスト

    error = Color(0xFFB00020),          // エラー色
    onError = Color.White
)

@Composable
fun Swipe01Theme(
    darkTheme: Boolean = false,  // ← 常に白基調に固定
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> CustomLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}