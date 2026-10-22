package com.example.plain_bulleted_list

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.EntryKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object PlainBulletedListKey : EntryKey {
    override val title = "[0] 기본 Bulleted List"
    override val description = """
        BulletedListOnTextField 프로젝트의 "option-state-drawn-bullet" 모듈을 가져온 후 살짝만 다듬음 (기능 변화는 없음)
        
        Refs: https://github.com/Kanmanemone/android-practice/tree/master/playground/BulletedListOnTextField
    """.trimIndent()
    override val screen: @Composable () -> Unit = { PlainBulletedListScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object PlainBulletedListModule {
    @Provides
    @IntoSet
    fun provideOptionStateDrawnBulletKey(): EntryKey = PlainBulletedListKey
}
