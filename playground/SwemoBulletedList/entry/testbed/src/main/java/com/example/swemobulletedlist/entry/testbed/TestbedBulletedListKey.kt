package com.example.swemobulletedlist.entry.testbed

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.EntryKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object TestbedBulletedListKey : EntryKey {
    override val title = "[3] 추가 예정"
    override val description = """
        [2]번 구현을 그대로 옮김 (모듈명, 파일명 등의 이름 제외 동일). 이 상태에서 한번 커밋 후 구현 착수.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { TestbedBulletedListScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object TestbedBulletedListModule {
    @Provides
    @IntoSet
    fun provideTestbedBulletedListKey(): EntryKey = TestbedBulletedListKey
}
