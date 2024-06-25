package com.example.useapplication

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
        /* application을 get하고 MyApplication으로 타입 캐스팅.
         * 타입 캐스팅의 이유:
         * application 객체는 기본적으로 Application 타입으로 제공되지만,
         * 실제로는 MyApplication 클래스의 인스턴스이기 때문이다.
         */
        val car = (application as MyApplication).carComponent.getCar()
        car.startCar()
    }
}