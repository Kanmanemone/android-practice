package com.example.splitlabeltext.entry.optionsentinel.di

import androidx.compose.runtime.Composable
import com.example.splitlabeltext.entry.api.MyNavKey
import com.example.splitlabeltext.entry.optionsentinel.OptionSentinelScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionSentinelKey : MyNavKey {
    override val title = "[1] Sentinel 써서 개행 시 새 컴포넌트"
    override val description = """
        한 필드 안에서 줄을 바꾸지 않고, 개행할 때마다 입력 필드(컴포넌트)를 새로 만든다.

        • Enter: 커서 뒤 텍스트를 들고 아래에 새 필드가 생긴다.
        • 여러 줄 붙여넣기: 줄 수만큼 새 필드가 생긴다.
        • 필드 맨 앞에서 Backspace: 그 필드를 없애고 내용을 윗 필드 끝에 붙인다.

        문제는 마지막 동작이다. Android는 빈 필드에서 누른 Backspace를 키 이벤트로 알려주지 않아서, 필드를 지워야 할 순간을 알 수 없다.

        그래서 필드마다 맨 앞에 보이지 않는 문자(zero-width space)를 하나 깔아둔다. 이 문자가 Sentinel이다. Sentinel이 지워지면 "맨 앞에서 Backspace를 눌렀다"고 판단한다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionSentinelScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionSentinelModule {
    @Provides
    @IntoSet
    fun provideOptionSentinelKey(): MyNavKey = OptionSentinelKey
}
