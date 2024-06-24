package com.example.parameter

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MyModule(private val countryOfManufacture: String) {

    @Provides
    @Named("Airbag")
    fun providesAirbagManufacturer(@Named("material") material: String): String {
        return "KENEL" + " (재질: ${material})" + " (MADE IN ${countryOfManufacture})"
    }

    @Provides
    @Named("Battery")
    fun providesBatteryManufacturer(@Named("type") type: String): String {
        return "TISTORY" + " (종류: ${type})" + " (MADE IN ${countryOfManufacture})"
    }
}