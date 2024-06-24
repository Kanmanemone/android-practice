package com.example.interfacedependency

import dagger.Component

@Component(modules = [BatteryModule::class])
interface CarComponent {
    fun getCar(): Car
}