package com.example.interfacedependency

import android.util.Log
import javax.inject.Inject

class Piston @Inject constructor(
    private val crankshaft: Crankshaft,
    private val cylinder: Cylinder
) {
    fun startPiston() {
        crankshaft.startCrankshaft()
        cylinder.startCylinder()
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}