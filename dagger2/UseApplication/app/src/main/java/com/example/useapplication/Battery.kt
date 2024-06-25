package com.example.useapplication

import android.util.Log
import javax.inject.Inject

class Battery @Inject constructor() {
    fun startBattery() {
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}