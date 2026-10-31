package com.example.swemobulletedlist.entry.refactored

import androidx.compose.runtime.Composable
import com.example.swemobulletedlist.entry.api.EntryKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object RefactoredBulletedListKey : EntryKey {
    override val title = "[1] 문단 단위로 정리한 Bulleted List"
    override val description = """
        화면과 동작은 [0]과 같다. 이후 기능을 얹기 쉽도록 코드만 정리한다.

        문단을 나누는 코드를 forEachParagraph 하나로 모은다. 맨 앞 보이지 않는 문자(sentinel)는 이 함수 안에 감춰, 호출부가 몰라도 된다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { RefactoredBulletedListScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object RefactoredBulletedListModule {
    @Provides
    @IntoSet
    fun provideRefactoredBulletedListKey(): EntryKey = RefactoredBulletedListKey
}
