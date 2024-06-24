package com.example.parameter

import android.util.Log
import javax.inject.Inject

class Cylinder @Inject constructor() {
    fun startCylinder() {
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}