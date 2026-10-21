package com.example.swemobulletedlist.entry.optionstatedashbullet

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStateDashBulletKey : MyNavKey {
    override val title = "[3] 알약(Pill) + 알약 색 대시(-) Bullet"
    override val description = """
         [2]과 같고, Bullet만 점(•) 대신 알약 배경색의 대시(-)로 그린다.

         대시는 '-' 글자보다 조금 길고 두꺼운, 양 끝이 둥근 가로 막대다. 크기는 글자 크기(em) 비율이라 글꼴 크기를 바꿔도 같이 커진다.

         대시 길이·두께·글자와의 간격은 DashBulletStyle로 정한다. 색은 PillStyle.color를 그대로 쓴다.

         아래는 [2]과 같다.

         모든 문단을 TextIndent 하나로 들여써 Bullet 자리를 낸다. 텍스트는 일반 TextField처럼 레이아웃 하나다.

         문단 맨 앞이 'xxx: '이면 xxx를 알약으로 보여 주고, 그 문단은 Bullet 대신 알약이 구분자 역할을 한다.

         알약은 저장하지 않고 텍스트에서 매번 찾는다. ': '를 치는 순간 알약이 생기고, 그 공백을 지우면 풀린다.

         알약은 글자 색과 배경만 바꿔 그린다. 레이아웃이 일반 문단과 같아, 알약이 생겨도 글자도 커서도 움직이지 않는다.

         알약은 'xxx:'까지만 덮어 본문과 한 칸 띄운다. 라벨 앞뒤 여백이 투명한 ':' 폭으로 같아 라벨이 가운데에 온다.

         알약은 보이는 모양일 뿐이라 라벨도 일반 글자처럼 커서·선택·드래그가 된다. 알약 뒤에서 Backspace를 누르면 공백이 지워져 알약이 풀린다.

         탭·드래그·선택 손잡이·복사·붙여넣기는 일반 TextField와 똑같이 동작한다. 따로 보정하는 코드가 없다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionStateDashBulletScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionStateDashBulletModule {
    @Provides
    @IntoSet
    fun provideOptionStateDashBulletKey(): MyNavKey = OptionStateDashBulletKey
}
