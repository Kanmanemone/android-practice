package com.example.migrationtohilt

import android.util.Log
import javax.inject.Inject

class TitaniumPiston @Inject constructor(
    private val crankshaft: Crankshaft,
    private val cylinder: Cylinder
) : Piston {
    override fun startPiston() {
        crankshaft.startCrankshaft()
        cylinder.startCylinder()
        Log.i("interfacer_han", "${this::class.simpleName} is ready")
    }
}