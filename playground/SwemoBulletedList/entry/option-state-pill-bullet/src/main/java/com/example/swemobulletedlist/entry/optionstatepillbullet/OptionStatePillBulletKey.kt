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

         알약 문단은 Bullet을 그리지 않고, 알약이 Bullet 자리에서 시작한다. 넘친 줄은 일반 문단의 글자 시작선에 맞춘다.

         알약이 생겨도 커서는 제자리다. 알약은 커서 바로 왼쪽까지 차지하고, 라벨은 알약 가운데에 온다.

         커서는 알약 안에 들어가지 않는다. 알약 뒤에서 Backspace를 누르면 공백이 지워져 알약이 풀린다.

         알약 배경은 텍스트 영역 왼쪽 밖(decorator나 바깥 padding 자리)까지 넘칠 수 있다.

         알약 색, 넘친 줄 들여쓰기(Bullet 자리 또는 알약 뒤 본문 시작선), 넘침 폭은 PillStyle로 정한다.
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
