package com.example.dagger2basics

import android.util.Log
import javax.inject.Inject

class Airbag @Inject constructor() {
    fun startAirbag() {
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}