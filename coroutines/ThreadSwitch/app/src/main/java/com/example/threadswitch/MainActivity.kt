package com.example.threadswitch

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private var count = 0
    private lateinit var tvCount: TextView
    private lateinit var btnCount: Button
    private lateinit var btnDwCoroutine: Button
    private lateinit var tvProgress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCount = findViewById(R.id.tvCount)
        btnCount = findViewById(R.id.btnCount)
        btnDwCoroutine = findViewById(R.id.btnDwCoroutine)
        tvProgress = findViewById(R.id.tvProgress)

        btnCount.setOnClickListener {
            tvCount.text = count++.toString()
        }

        btnDwCoroutine.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                downloadData()
            }
        }
    }

    private suspend fun downloadData() {
        for (i in 1..100000) {
            withContext(Dispatchers.Main) {
                tvProgress.text = "진행도: $i"
            }
        }
    }
}