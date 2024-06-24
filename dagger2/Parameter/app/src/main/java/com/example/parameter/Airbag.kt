package com.example.parameter

import android.util.Log
import javax.inject.Inject
import javax.inject.Named

class Airbag @Inject constructor(@Named("Airbag") private val manufacturer: String) {
    fun startAirbag() {
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
        Log.i("interfacer_han", "${this::class.simpleName} made by ${manufacturer}")
    }
}