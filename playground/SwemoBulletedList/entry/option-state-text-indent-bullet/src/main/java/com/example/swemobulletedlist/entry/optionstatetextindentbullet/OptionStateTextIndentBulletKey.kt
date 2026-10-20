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
         (작업 중) 아직은 [0]와 동일하다.
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
