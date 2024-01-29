package com.example.activitylifecyclelogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("interfacer_han", "MainActivity.onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val switchActivityButton = findViewById<Button>(R.id.switchActivityButton)
        switchActivityButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            /* FLAG_ACTIVITY_REORDER_TO_FRONT 플래그는 호출하려는 액티비티가 이미 스택에 존재하는 경우,
             * 해당 액티비티를 스택의 맨 위로 이동시키고 기존 인스턴스를 재사용하도록 함.
             * 이 플래그가 없으면, Activity를 전환할 때마다 onCreate()됨.
             */
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    override fun onStart() {
        Log.i("interfacer_han", "MainActivity.onStart()")
        super.onStart()
    }

    override fun onResume() {
        Log.i("interfacer_han", "MainActivity.onResume()")
        super.onResume()
    }

    override fun onPause() {
        Log.i("interfacer_han", "MainActivity.onPause()")
        super.onPause()
    }

    override fun onStop() {
        Log.i("interfacer_han", "MainActivity.onStop()")
        super.onStop()
    }

    override fun onRestart() {
        Log.i("interfacer_han", "MainActivity.onRestart()")
        super.onRestart()
    }

    override fun onDestroy() {
        Log.i("interfacer_han", "MainActivity.onDestroy()")
        super.onDestroy()
    }
}