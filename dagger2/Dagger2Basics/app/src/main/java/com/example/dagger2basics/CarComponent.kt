package com.example.dagger2basics

import dagger.Component

@Component
interface CarComponent {
    fun getCar(): Car
}