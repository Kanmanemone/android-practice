package com.example.swemobulletedlist.entry.home

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.EntryKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object HomeKey : EntryKey {
    override val title = "구현 방법 목록"
    override val screen: @Composable () -> Unit = { HomeScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {
    @Provides
    @IntoSet
    fun provideHomeKey(): EntryKey = HomeKey
}
