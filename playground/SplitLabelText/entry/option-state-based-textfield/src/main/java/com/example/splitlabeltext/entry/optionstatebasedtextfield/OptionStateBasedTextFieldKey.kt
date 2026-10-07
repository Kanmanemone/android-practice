package com.example.splitlabeltext.entry.optionstatebasedtextfield

import androidx.compose.runtime.Composable
import com.example.splitlabeltext.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStateBasedTextFieldKey : MyNavKey {
    override val title = "[2] State-based TextField"
    override val description = """
        한 줄 한 줄 마다 컴포넌트를 만들어 배치하는 방식은 자원 낭비가 심하고, 실제로 프레임 드랍도 일어남을 확인했다.
        
        단일 TextField 컴포넌트를 사용하는 방식을 고려해본다. 마침, Jetpack Compose에 새 API를 쓰는 TextField가 업데이트 되었다.
        
        이 TextField는 전용 State를 내려받는데, 그 State를 조작함으로써 더 수월히 Text 로직을 처리할 수 있다고 한다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionStateBasedTextFieldScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionStateBasedTextFieldModule {
    @Provides
    @IntoSet
    fun provideOptionStateBasedTextFieldKey(): MyNavKey = OptionStateBasedTextFieldKey
}
