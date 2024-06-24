package com.example.interfacedependency

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
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

        val car = DaggerCarComponent.create().getCar()
        car.startCar()
    }
}
