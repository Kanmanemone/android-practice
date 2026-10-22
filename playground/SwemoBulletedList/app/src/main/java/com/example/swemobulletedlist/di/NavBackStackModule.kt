package com.example.swemobulletedlist.di

import androidx.navigation3.runtime.NavBackStack
import com.example.swemobulletedlist.entry.api.EntryKey
import com.example.swemobulletedlist.entry.home.HomeKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavBackStackModule {
    @Provides
    @Singleton
    fun provideNavBackStack(): NavBackStack<EntryKey> = NavBackStack(HomeKey)
}
