package com.example.provides

import android.util.Log
import javax.inject.Inject
import javax.inject.Named

class Battery @Inject constructor(@Named("Battery") private val manufacturer: String) {
    fun startBattery() {
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
        Log.i("interfacer_han", "${this::class.simpleName} made by ${manufacturer}")
    }
}