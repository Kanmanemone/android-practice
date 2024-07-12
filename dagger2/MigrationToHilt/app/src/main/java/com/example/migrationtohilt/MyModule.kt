package com.example.migrationtohilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
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