package com.example.swemobulletedlist.entry.optionstatedrawnbullet

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.LineHeightStyle
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
    /**
     * 테마의 bodyLarge를 바탕으로 한 글자 스타일.
     *
     * - 글자 크기·줄 높이는 테마 값(sp)을 그대로 쓴다. 시스템 글꼴 크기 설정에 따라 함께 커지고 작아진다.
     * - `color`: BasicTextField는 LocalContentColor를 읽지 않으므로 직접 넣는다.
     * - `lineHeightStyle`
     *     - `Trim.None`: 모든 줄 상자 높이를 같게 한다. [BulletListTextField]는 불릿을 줄 상자 가운데에 그리므로 이게 필요하다.
     *     - `Alignment.Center`: 한글처럼 대체 글꼴이 섞여도 글자가 줄 안에서 덜 흔들린다.
     */
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        color = LocalContentColor.current,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
    )

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
