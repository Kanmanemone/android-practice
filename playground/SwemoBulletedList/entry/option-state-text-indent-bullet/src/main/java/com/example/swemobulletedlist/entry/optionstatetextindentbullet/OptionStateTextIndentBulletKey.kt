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

         (작업 중) 여러 줄 선택 시 하이라이트가 Bullet을 덮는 문제가 아직 남아 있다.
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
