package com.example.splitlabeltext.entry.optionstatedrawnbullet

import androidx.compose.runtime.Composable
import com.example.splitlabeltext.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStateDrawnBulletKey : MyNavKey {
    override val title = "[4] State-based + 그려서 붙인 Bullet"
    override val description = """
        [3]은 OutputTransformation으로 불릿 글자를 화면 텍스트에 끼워 넣었다. 그런데 화면 텍스트가 저장값보다 길어지면, Samsung 키보드의 Backspace(IME 삭제 명령) 처리 중 크래시가 난다 (foundation 1.10.4 버그).

        그래서 불릿을 글자가 아니라 그림으로 붙인다.

        • 줄마다 불릿 폭만큼 들여쓰고(textIndent), 그 빈자리에 불릿을 그린다.
        • 화면 텍스트가 저장값과 똑같아 크래시가 나지 않고, 커서 위치 보정도 필요 없다.
        • 긴 줄이 넘어가도 글자 시작선에 맞춰 들여쓰기된다.

        맨 앞 sentinel(폭 0인 문자)은 그대로 둔다. 빈 첫 줄에서 누른 Backspace를 감지하는 데 쓴다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionStateDrawnBulletScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionStateDrawnBulletModule {
    @Provides
    @IntoSet
    fun provideOptionStateDrawnBulletKey(): MyNavKey = OptionStateDrawnBulletKey
}
