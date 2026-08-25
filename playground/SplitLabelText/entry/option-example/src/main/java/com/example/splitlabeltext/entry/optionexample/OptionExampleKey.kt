package com.example.splitlabeltext.entry.optionexample

import androidx.compose.runtime.Composable
import com.example.splitlabeltext.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionExampleKey : MyNavKey {
    override val title = "(예시) 독립된 두 필드"
    override val screen: @Composable () -> Unit = { OptionExampleScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionExampleModule {
    @Provides
    @IntoSet
    fun provideOptionExampleKey(): MyNavKey = OptionExampleKey
}
