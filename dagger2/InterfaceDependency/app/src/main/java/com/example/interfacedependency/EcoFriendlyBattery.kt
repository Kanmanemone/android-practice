package com.example.interfacedependency

import android.util.Log
import javax.inject.Inject

class EcoFriendlyBattery @Inject constructor() : Battery {
    override fun startBattery() {
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}