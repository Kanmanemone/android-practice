package com.example.migrationtohilt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var car: Car

    @Inject
    lateinit var carFactory: Car.Factory

    @Inject
    lateinit var engine: Engine

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

//      DaggerCarComponent.create().inject(this)

        car = carFactory.create(engine) // 런타임 때 Engine 객체 주입
        car.startCar()
    }
}