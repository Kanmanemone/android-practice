package com.example.swemobulletedlist.entry.optionstatepillbullet

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStatePillBulletKey : MyNavKey {
    override val title = "[2] State-based + 알약(Pill)이 Bullet을 대신하는 문단"
    override val description = """
         [1]처럼 문단마다 TextIndent로 Bullet 자리를 내고 Bullet을 그린다.

         문단 맨 앞이 'xxx: '이면 xxx를 알약으로 보여 주고, 그 문단은 Bullet 대신 알약이 구분자 역할을 한다.

         알약은 저장하지 않고 텍스트에서 매번 찾는다. ': '를 치는 순간 알약이 생기고, 그 공백을 지우면 풀린다.

         (작업 중) 아직 Bullet이 알약 옆에 남아 있다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionStatePillBulletScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionStatePillBulletModule {
    @Provides
    @IntoSet
    fun provideOptionStatePillBulletKey(): MyNavKey = OptionStatePillBulletKey
}
