package com.example.migrationtohilt

import android.util.Log
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class Car @AssistedInject constructor(
    @Assisted private val engine: Engine,
    private val airbag: Airbag,
    private val battery: Battery
) {
    fun startCar() {
        engine.startEngine()
        airbag.startAirbag()
        battery.startBattery()
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }

    @AssistedFactory
    fun interface Factory {
        fun create(engine: Engine): Car
    }
}