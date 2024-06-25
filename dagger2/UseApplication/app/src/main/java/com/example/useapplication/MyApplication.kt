package com.example.useapplication

import android.app.Application

class MyApplication : Application() {
    lateinit var carComponent: CarComponent

    override fun onCreate() {
        super.onCreate()
        carComponent = initDagger()
    }

    private fun initDagger(): CarComponent {
        return DaggerCarComponent.create()
    }
}