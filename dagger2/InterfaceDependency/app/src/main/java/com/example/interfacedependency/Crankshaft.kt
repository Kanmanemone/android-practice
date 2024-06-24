package com.example.interfacedependency

import android.util.Log
import javax.inject.Inject

class Crankshaft @Inject constructor() {
    fun startCrankshaft() {
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}