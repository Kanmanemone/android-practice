package com.example.provides

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MyModule {

    @Provides
    @Named("Airbag")
    fun providesAirbagManufacturer(productCode: Int): String {
        return "KENEL-$productCode"
    }

    @Provides
    @Named("Battery")
    fun providesBatteryManufacturer(): String {
        return "TISTORY"
    }

    @Provides
    fun providesAirbagProductCode(): Int {
        return 20260822
    }
}