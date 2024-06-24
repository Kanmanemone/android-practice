package com.example.provides

import dagger.Component

@Component(modules = [MyModule::class])
interface CarComponent {
    fun getCar(): Car
}