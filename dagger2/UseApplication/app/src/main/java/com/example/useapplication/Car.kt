package com.example.useapplication

import android.util.Log
import javax.inject.Inject

class Car @Inject constructor(
    private val engine: Engine,
    private val airbag: Airbag,
    private val battery: Battery
) {
    fun startCar() {
        engine.startEngine()
        airbag.startAirbag()
        battery.startBattery()
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}