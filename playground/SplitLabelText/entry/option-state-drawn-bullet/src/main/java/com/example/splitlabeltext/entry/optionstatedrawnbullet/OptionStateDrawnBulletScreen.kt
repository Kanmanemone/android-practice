package com.example.splitlabeltext.entry.optionstatedrawnbullet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 상태([rememberBulletListTextFieldState])를 만들어 컴포넌트([BulletListTextField])에 넘긴다.
 *
 * - 테마에 맞춘 글자 스타일과 커서 색도 여기서 정해 내려준다. [BulletListTextField]의 기본값은 `BasicTextField`와 같아 테마를 모른다.
 */
@Composable
fun OptionStateDrawnBulletScreen(
    modifier: Modifier = Modifier,
    initialText: String = "Sample Text",
) {
    val state = rememberBulletListTextFieldState(initialText)
    val textStyle = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current) // BasicTextField는 LocalContentColor를 읽지 않으므로 직접 넣는다

    BulletListTextField(
        state = state,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        textStyle = textStyle,
        cursorBrush = SolidColor(textStyle.color), // 기본값(검정)은 다크모드에서 안 보인다
    )
}

@Preview(name = "빈 목록", showBackground = true)
@Composable
private fun OptionStateDrawnBulletScreenEmptyPreview() {
    OptionStateDrawnBulletScreen(initialText = "")
}

@Preview(name = "항목 여러 개", showBackground = true)
@Composable
private fun OptionStateDrawnBulletScreenFilledPreview() {
    OptionStateDrawnBulletScreen(initialText = "우유 사기\n계란 사기\n빵 사기")
}
