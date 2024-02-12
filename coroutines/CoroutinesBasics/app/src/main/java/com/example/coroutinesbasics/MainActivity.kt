package com.example.coroutinesbasics

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var count = 0
    private lateinit var tvCount: TextView
    private lateinit var btnCount: Button
    private lateinit var btnDw: Button
    private lateinit var btnDwCoroutine: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvCount = findViewById(R.id.tvCount)
        btnCount = findViewById(R.id.btnCount)
        btnDw = findViewById(R.id.btnDw)
        btnDwCoroutine = findViewById(R.id.btnDwCoroutine)

        btnCount.setOnClickListener {
            tvCount.text = count++.toString()
        }

        btnDw.setOnClickListener {
            downloadData()
        }

        btnDwCoroutine.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                downloadData()
            }
        }
    }

    private fun downloadData() {
        for (i in 1..100000) {
            Log.i("interfacer_han", "Downloading data $i in ${Thread.currentThread().name}")
        }
    }
}