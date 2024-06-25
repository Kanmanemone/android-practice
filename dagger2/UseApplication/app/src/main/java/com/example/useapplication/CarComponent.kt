package com.example.useapplication

import dagger.Component

@Component
interface CarComponent {
    fun getCar(): Car
}