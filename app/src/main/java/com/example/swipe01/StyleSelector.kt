package com.example.swipe01

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StyleSelector(
    buttonStyleIndex: Int,
    onButtonStyleChange: (Int) -> Unit,
    backgroundStyleIndex: Int,
    onBackgroundStyleChange: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // ボタンスタイル選択
        Text("ボタンスタイルを選択:")
        DropdownSelector(
            items = (0..9).map { "スタイル $it" },
            selectedIndex = buttonStyleIndex,
            onItemSelected = onButtonStyleChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 背景スタイル選択
        Text("背景スタイルを選択:")
        DropdownSelector(
            items = (0..9).map { "背景 $it" },
            selectedIndex = backgroundStyleIndex,
            onItemSelected = onBackgroundStyleChange
        )
    }
}

@Composable
fun DropdownSelector(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(items[selectedIndex])
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

