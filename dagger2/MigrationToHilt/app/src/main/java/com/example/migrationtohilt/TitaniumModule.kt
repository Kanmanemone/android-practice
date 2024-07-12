package com.example.migrationtohilt

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/*

// @Provides를 사용하는 경우
@Module
@InstallIn(SingletonComponent::class)
class PistonModule {

    @Provides
    fun providesTitaniumPiston(titaniumPiston: TitaniumPiston) : Piston {
        return titaniumPiston
    }
}

*/

// @Binds를 사용하는 경우
@Module
@InstallIn(SingletonComponent::class)
abstract class PistonModule {

    @Binds
    abstract fun providesTitaniumPiston(titaniumPiston: TitaniumPiston): Piston
}