package com.example.splitlabeltext.entry.optionsentinel.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle

/** 각 항목 앞에 붙는 Bullet (글머리 기호). 뒤 간격까지 포함한다. */
private const val BULLET = "•  "

/**
 * 상태(State)를 갖지 않고 꾸밈만 담당하는 컴포넌트.
 *
 * - [BULLET] 표시도 담당한다.
 * - [modifier]는 내부 BasicTextField 에 걸린다 (focusRequester 등).
 */
@Composable
internal fun BulletTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = bulletTextStyle()
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Text(text = BULLET, style = style)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.weight(1f),
            textStyle = style,
            cursorBrush = SolidColor(style.color), // 기본값(검정)은 다크모드에서 안 보인다
        )
    }
}

/**
 * 불릿 항목에서 공통으로 사용하는 텍스트 스타일.
 * 글머리 [Text]와 [BasicTextField]가 동일한 줄 높이와 정렬을 사용하도록 맞춘다.
 *
 * - `MaterialTheme.typography.bodyLarge`: 기본 글꼴 크기, 굵기, 줄 높이 등을 가져온다.
 * - `LocalContentColor`: [BasicTextField]는 이 값을 스스로 읽지 않고 검정으로 그린다. 그래서 다크모드에서도 테마 색을 따르도록 직접 넣는다.
 * - `includeFontPadding = false`: Android에선 키가 큰 글자 등을 제대로 표시하려고 이 옵션을 두지만, 기본값은 false다. 여기서는 명시적으로 지정한다.
 * - `LineHeightStyle.Alignment.Center`: 설정된 줄 높이 안에서 글자를 세로 중앙에 배치한다.
 * - `LineHeightStyle.Trim.None`: 첫 줄 위와 마지막 줄 아래의 줄 높이 여백을 잘라내지 않는다.
 */
@Composable
private fun bulletTextStyle(): TextStyle =
    MaterialTheme.typography.bodyLarge.copy(
        color = LocalContentColor.current,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.None,
        ),
    )
