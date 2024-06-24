package com.example.activitydependencyinjection

import android.util.Log
import javax.inject.Inject

class Engine @Inject constructor(private val piston: Piston) {
    fun startEngine() {
        piston.startPiston()
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}