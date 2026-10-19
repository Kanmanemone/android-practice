package com.example.swemobulletedlist.entry.optionstatedrawnbullet

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStateDrawnBulletKey : MyNavKey {
    override val title = "[0] State-based + 그려서 붙인 Bullet"
    override val description = """
         BulletedListOnTextField의 [3]과는 달리, Bullet을 텍스트 영역에 넣지 않고, 별도로(= 오버레이 느낌으로) '그려낸다'. 따라서 둘이 섞일 여지가 없다.
         
         그렇기에 Transformation 과정에 Bullet 처리 로직 또한 없다. 단순하기에 더 정교한 구현 방식인 셈이다.
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
