package com.example.swemobulletedlist.entry.pill

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.EntryKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object PillBulletedListKey : EntryKey {
    override val title = "[2] 문단 맨 앞 라벨을 Pill로"
    override val description = """
        [1]과 같이 Bullet을 그린다.

        문단 맨 앞이 'xxx: '이면 라벨 xxx를 Pill로 감싸 그린다. Pill은 Bullet을 대신하지 않는다. Bullet은 그대로 두고 그 뒤 라벨에 Pill을 씌운다.

        Pill은 저장하지 않고 텍스트에서 매번 찾는다. ': '를 치는 순간 Pill이 생기고, 그 공백을 지우면 풀린다.

        Pill은 글자 색과 배경만 바꿔 그린다. 레이아웃이 일반 문단과 같아, Pill이 생겨도 글자도 커서도 움직이지 않는다.

        Pill은 'xxx:'까지만 덮어 본문과 한 칸 띄운다. 라벨 앞뒤 여백이 투명한 ':' 폭으로 같아 라벨이 가운데에 온다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { PillBulletedListScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object PillBulletedListModule {
    @Provides
    @IntoSet
    fun providePillBulletedListKey(): EntryKey = PillBulletedListKey
}
