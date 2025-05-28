package com.example.swipe01

import android.os.Bundle
import android.widget.Button
import android.util.Log
import androidx.activity.ComponentActivity

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ropeView = findViewById<RopeView>(R.id.RopeView)
        val resetButton = findViewById<Button>(R.id.resetButton)

        resetButton.setOnClickListener {
            ropeView.resetRope()
        }

        Log.d("SecondActivity", "SecondActivity started")
    }
}
