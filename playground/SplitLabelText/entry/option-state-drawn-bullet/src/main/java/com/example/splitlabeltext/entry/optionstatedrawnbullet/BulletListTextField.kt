package com.example.splitlabeltext.entry.optionstatedrawnbullet

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Density

/** 각 줄 앞에 그리는 Bullet (글머리 기호). 뒤 간격까지 포함한다. */
private const val BULLET = "•  "

/**
 * 단일 [BasicTextField] 하나로 그리는 불릿 리스트 입력 필드.
 *
 * - 한 줄이 곧 한 항목이다. 개행으로 항목을 나누고, 줄 맨 앞 Backspace로 합친다.
 * - 불릿은 텍스트가 아니라 그림이다. 줄마다 [BULLET] 폭만큼 들여쓰고(`textIndent`), 그 빈자리에 불릿을 그린다.
 *     - 화면 텍스트가 저장값과 똑같아서, 커서 위치 보정도 IME 삭제 크래시(foundation 1.10.4 버그)도 신경 쓸 필요가 없다.
 *     - 긴 줄이 넘어가도 글자 시작선에 맞춰 들여쓰기된다.
 * - 파라미터는 `BasicTextField(state)`와 같다. 단, `inputTransformation`과 `outputTransformation`은 없다.
 *     - `inputTransformation`은 sentinel 규칙(`BulletListTransformation.kt`)이 차지한다.
 *     - `outputTransformation`은 화면 텍스트 길이를 바꾸면 위 크래시가 나므로 열어두지 않는다.
 * - [state]는 [rememberBulletListTextFieldState]로 만든 것이어야 한다. 맨 앞 sentinel을 전제로 동작하기 때문이다.
 * - 기본값도 `BasicTextField`와 같다. 테마 색(다크모드 등)이 필요하면 호출부가 [textStyle], [cursorBrush]로 넘긴다.
 */
@Composable
internal fun BulletListTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(Color.Black), // BasicTextFieldDefaults.CursorBrush와 같은 값 (그 객체는 private이라 직접 못 쓴다)
    decorator: TextFieldDecorator? = null,
    scrollState: ScrollState = rememberScrollState(),
) {
    /** 불릿 한 개를 [textStyle]로 측정한 결과. 들여쓰기 폭과 그리기에 같이 쓴다. */
    val textMeasurer = rememberTextMeasurer()
    val bulletLayout = remember(textMeasurer, textStyle) { textMeasurer.measure(BULLET, textStyle) }

    /** 모든 줄을 불릿 폭만큼 들여쓴 스타일. 첫 줄(firstLine)과 줄바꿈된 줄(restLine) 모두 같은 폭이다. */
    val indent = with(LocalDensity.current) { bulletLayout.size.width.toSp() }
    val indentedStyle = textStyle.merge(TextStyle(textIndent = TextIndent(firstLine = indent, restLine = indent)))

    /**
     * 최신 텍스트 레이아웃을 꺼내는 함수. 불릿을 그릴 줄 위치를 여기서 얻는다.
     *
     * - [BasicTextField]의 onTextLayout으로 받아 둔다. 그리기 단계에서만 읽으므로 레이아웃이 바뀌면 다시 그리기만 일어난다.
     */
    var getTextLayout: (() -> TextLayoutResult?)? by remember { mutableStateOf(null) }

    BasicTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        inputTransformation = BulletListInputTransformation,
        textStyle = indentedStyle,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = { getResult ->
            getTextLayout = getResult
            onTextLayout?.invoke(this, getResult) // 호출부가 넘긴 콜백도 그대로 불러준다
        },
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorator = { innerTextField ->
            /** 텍스트 영역 뒤에 불릿을 그린다. 호출부의 decorator가 있으면 그 안쪽에 들어간다. */
            val bulletedTextField = @Composable {
                Box(
                    Modifier
                        .clipToBounds() // 스크롤로 밀려난 불릿이 영역 밖에 그려지지 않게
                        .drawBehind {
                            getTextLayout?.invoke()?.let { layout ->
                                val scrollOffset = scrollState.value.toFloat()
                                val scroll = if (lineLimits == TextFieldLineLimits.SingleLine) {
                                    Offset(-scrollOffset, 0f) // 한 줄 모드는 가로로 스크롤된다
                                } else {
                                    Offset(0f, -scrollOffset) // 여러 줄 모드는 세로로 스크롤된다
                                }
                                drawBullets(layout, bulletLayout, scroll)
                            }
                        },
                ) {
                    innerTextField()
                }
            }
            decorator?.Decoration(bulletedTextField) ?: bulletedTextField()
        },
        scrollState = scrollState,
    )
}

/**
 * 각 줄(문단)의 시작 위치에 불릿을 그린다.
 *
 * - 줄의 시작은 텍스트 맨 앞, 그리고 `\n` 바로 뒤다.
 * - 줄바꿈으로 넘어간 줄에는 그리지 않는다. 불릿은 항목마다 하나다.
 * - 불릿은 들여쓰기로 비워 둔 왼쪽 빈자리(x = 0)에, 그 줄의 글자 기준선(baseline)에 맞춰 그린다.
 *     - 윗선(lineTop)에 맞추면 어긋난다. 첫 줄은 줄 높이 윗부분이 잘려(trim) 그려지고 나머지 줄은 아니라서, 줄마다 글자와 윗선 사이 간격이 다르다.
 */
private fun DrawScope.drawBullets(
    layout: TextLayoutResult,
    bulletLayout: TextLayoutResult,
    scroll: Offset,
) {
    val text = layout.layoutInput.text
    var lineStart = 0
    while (true) {
        val line = layout.getLineForOffset(lineStart)
        val top = layout.getLineBaseline(line) - bulletLayout.firstBaseline // 불릿의 기준선을 그 줄의 기준선에 맞춘다
        drawText(bulletLayout, topLeft = Offset(0f, top) + scroll)

        val newline = text.indexOf('\n', startIndex = lineStart)
        if (newline < 0) break
        lineStart = newline + 1
    }
}
