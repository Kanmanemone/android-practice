package com.example.activitydependencyinjection

import dagger.Component

@Component
interface CarComponent {
    fun inject(mainActivity: MainActivity)
}