package com.example.lifecyclescope

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("interfacer_han", "MainActivity.onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.i("interfacer_han", "(lifecycleScope) repeat when onStart()")
            }

            lifecycle.repeatOnLifecycle(Lifecycle.State.DESTROYED) {
                Log.i("interfacer_han", "(lifecycleScope) repeat when onStart()")
            }
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

/* Log 출력 결과 - 앱 최초 실행 시
MainActivity.onCreate()
MainActivity.onStart()
(lifecycleScope) repeat when onStart()
MainActivity.onResume()
*/

/* Log 출력 결과 - 앱을 벗어났을 때
MainActivity.onPause()
MainActivity.onStop()
*/

/* Log 출력 결과 - 앱에 재진입했을 때
MainActivity.onRestart()
MainActivity.onStart()
(lifecycleScope) repeat when onStart()
MainActivity.onResume()
*/