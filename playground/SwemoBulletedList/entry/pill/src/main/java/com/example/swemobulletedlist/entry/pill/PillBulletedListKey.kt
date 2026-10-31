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
    override val title = "[2] 추가 예정"
    override val description = """
        [1]번 구현을 그대로 옮김 (모듈명, 파일명 등의 이름 제외 동일). 이 상태에서 한번 커밋 후 구현 착수.
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
