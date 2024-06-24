package com.example.activitydependencyinjection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var car: Car

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.activity_main)
/*
        val crankshaft = Crankshaft()
        val cylinder = Cylinder()
        val piston = Piston(crankshaft, cylinder)
        val engine = Engine(piston)
        val airbag = Airbag()
        val battery = Battery()

        val car = Car(engine, airbag, battery)
*/
        DaggerCarComponent.create().inject(this)
        car.startCar()
    }
}