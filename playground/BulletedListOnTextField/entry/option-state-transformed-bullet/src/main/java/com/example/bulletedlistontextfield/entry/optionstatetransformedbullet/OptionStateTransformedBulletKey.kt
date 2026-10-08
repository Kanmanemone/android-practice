package com.example.bulletedlistontextfield.entry.optionstatetransformedbullet

import androidx.compose.runtime.Composable
import com.example.bulletedlistontextfield.entry.api.MyNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.Serializable

@Serializable
object OptionStateTransformedBulletKey : MyNavKey {
    override val title = "[3] State-based + 끼워 넣은 Bullet"
    override val description = """
        InputTransformation 및 OutputTransformation으로, 입력 및 출력을 가로채어 Bullet 문자를 삽입한(끼워넣는)다.
        
        치명적인 단점이 있다. 텍스트 전체 선택 → 복사 → 붙여넣기를 하면, Bullet까지 같이 붙여넣어진다. 이걸 어떻게든 우회할 수 있을지는 몰라도, 그 우회책 또한 잠재적인 에러를 발생시킬 것으로 예상한다.
        
        애초에, OutputTransformation은 전화번호 등을 입력할 때 괄호, 공백, "-" 따위를 넣기 쉽게하기 위한 용도라 한다. 나와는 처음부터 결이 맞지 않았는지도 모르겠다.
    """.trimIndent()
    override val screen: @Composable () -> Unit = { OptionStateTransformedBulletScreen() }
}

@Module
@InstallIn(SingletonComponent::class)
object OptionStateTransformedBulletModule {
    @Provides
    @IntoSet
    fun provideOptionStateTransformedBulletKey(): MyNavKey = OptionStateTransformedBulletKey
}
