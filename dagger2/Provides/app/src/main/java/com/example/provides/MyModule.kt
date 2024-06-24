package com.example.provides

import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MyModule {

    @Provides
    @Named("Airbag")
    fun providesAirbagManufacturer(): String {
        return "KENEL"
    }

    @Provides
    @Named("Battery")
    fun providesBatteryManufacturer(): String {
        return "TISTORY"
    }
}