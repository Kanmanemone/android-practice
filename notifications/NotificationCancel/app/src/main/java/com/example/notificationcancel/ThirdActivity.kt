package com.example.notificationcancel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.io.Serializable

class ThirdActivity : AppCompatActivity(), Serializable {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
    }
}