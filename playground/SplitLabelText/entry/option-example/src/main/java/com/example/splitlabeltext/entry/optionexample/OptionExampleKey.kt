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
    override val title = "[0] 예시용 스크린"
    override val description = """
        Label 필드와 Text 필드를 따로 둔 가장 단순한 형태의 예시다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionExampleScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionExampleModule {
    @Provides
    @IntoSet
    fun provideOptionExampleKey(): MyNavKey = OptionExampleKey
}
