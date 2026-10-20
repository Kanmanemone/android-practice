package com.example.swemobulletedlist.entry.optionstatetextindentbullet

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStateTextIndentBulletKey : MyNavKey {
    override val title = "[1] State-based + TextIndent로 자리를 낸 Bullet"
    override val description = """
         [0]와 같이 Bullet을 그리지만, 그 자리를 decorator의 padding이 아니라 TextIndent(문단 들여쓰기)로 비운다.

         들여쓰기는 문단마다 다르게 줄 수 있어, 이후 문단별로 Bullet 자리를 조정하는 토대가 된다.

         선택 하이라이트는 직접 그려, 여러 줄을 선택해도 Bullet 자리를 칠하지 않는다.

         문단마다 들여쓰기를 따로 건다. 확인용으로 '>'로 시작하는 문단은 들여쓰기가 2배이고, Bullet도 따라 움직인다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionStateTextIndentBulletScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionStateTextIndentBulletModule {
    @Provides
    @IntoSet
    fun provideOptionStateTextIndentBulletKey(): MyNavKey = OptionStateTextIndentBulletKey
}
