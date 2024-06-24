package com.example.interfacedependency

import dagger.Binds
import dagger.Module

@Module
abstract class BatteryModule {

    @Binds
    abstract fun bindsEcoFriendlyBattery(ecoFriendlyBattery: EcoFriendlyBattery): Battery
}

/*

@Module
class BatteryModule {

    @Provides
    fun providesEcoFriendlyBattery(ecoFriendlyBattery: EcoFriendlyBattery): Battery {
        return ecoFriendlyBattery
    }
}

*/