package com.example.splitlabeltext.entry.optionstatetransformedbullet

import androidx.compose.runtime.Composable
import com.example.splitlabeltext.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStateTransformedBulletKey : MyNavKey {
    override val title = "[3] State-based + 끼워 넣은 Bullet"
    override val description = """
        InputTransformation 및 OutputTransformation으로, 입력 및 출력을 가로채어 Bullet 문자를 삽입한(끼워넣는)다.
        
        * 런타임 버그로 인해 이 구현 방법은 폐기할랬는데, 다행히 androidx.compose.foundation:foundation 라이브러리가 업데이트되며 버그가 해결되었다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionStateTransformedBulletScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionStateTransformedBulletModule {
    @Provides
    @IntoSet
    fun provideOptionStateTransformedBulletKey(): MyNavKey = OptionStateTransformedBulletKey
}
