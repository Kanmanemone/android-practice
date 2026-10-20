package com.example.swemobulletedlist.entry.optionstatetextindentbullet

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
 * - 불릿은 텍스트가 아니라 그림이다. 모든 문단을 [BULLET] 폭만큼 들여쓰고(`textIndent`), 그 들여쓰기 자리에 불릿을 그린다.
 *     - 화면 텍스트가 저장값과 똑같아서, 커서 위치를 따로 보정할 필요가 없다.
 *     - 첫 줄(`firstLine`)과 넘친 줄(`restLine`)을 같은 폭으로 들여쓰므로, 긴 줄이 넘어가도 글자 시작선에 맞춰진다.
 *     - [0] drawn-bullet은 이 자리를 decorator의 padding으로 냈다. 들여쓰기는 문단마다 다르게 줄 수 있어서 그쪽으로 바꿨다.
 *     - 알려진 문제: 여러 줄을 선택하면 가운데 줄 하이라이트가 줄 전체 폭으로 칠해져 불릿을 덮는다.
 * - 파라미터는 `BasicTextField(state)`와 같다. 단, `inputTransformation`과 `outputTransformation`은 없다.
 *     - `inputTransformation`은 sentinel 규칙(`BulletListTransformation.kt`)이 차지한다.
 *     - `outputTransformation`은 마지막 빈 문단의 들여쓰기 보정(`BulletListTransformation.kt`)이 차지한다.
 * - [state]는 [rememberBulletListTextFieldState]로 만든 것이어야 한다. 맨 앞 sentinel을 전제로 동작하기 때문이다.
 * - 기본값도 `BasicTextField`와 같다. 테마 색(다크모드 등)이 필요하면 호출부가 [textStyle], [cursorBrush]로 넘긴다.
 * - [textStyle]의 `textIndent`는 무시된다. 불릿 자리를 내는 들여쓰기로 덮어쓰기 때문이다.
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

    /**
     * 모든 문단에 거는 들여쓰기. 폭은 불릿 한 개의 폭이다.
     *
     * - `TextIndent`는 sp 단위만 받으므로, 측정한 px 폭을 sp로 바꿔 넣는다.
     * - 문단마다 따로 걸지 않고 [textStyle]에 기본값으로 넣는다. 텍스트 끝의 빈 문단까지 빠짐없이 적용된다.
     */
    val bulletIndent = with(LocalDensity.current) { bulletLayout.size.width.toSp() }
    val indentedTextStyle = remember(textStyle, bulletIndent) {
        textStyle.copy(textIndent = TextIndent(firstLine = bulletIndent, restLine = bulletIndent))
    }

    var getTextLayout: (() -> TextLayoutResult?)? by remember { mutableStateOf(null) }

    BasicTextField(
        state = state,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        inputTransformation = BulletListInputTransformation,
        outputTransformation = BulletListOutputTransformation,
        textStyle = indentedTextStyle,
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
            /**
             * 텍스트 영역 뒤에 불릿을 그린다.
             *
             * - 호출부가 decorator를 넘겼다면, 이 Box가 그 decorator의 innerTextField 자리에 들어간다. 즉 불릿은 decorator 장식보다 안쪽이다.
             * - Box와 텍스트 레이아웃의 좌표가 같다. 들여쓰기는 텍스트 레이아웃 안에서 일어나므로, 불릿 위치도 레이아웃 좌표로 바로 구한다.
             */
            val bulletedTextField = @Composable {
                Box(
                    Modifier
                        .clipToBounds() // 스크롤로 밀려난 불릿이 영역(=Box) 밖에 그려지지 않게
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
                    propagateMinConstraints = true, // innerTextField가 글자 폭으로 줄지 않고 남은 영역을 꽉 채우게
                ) {
                    innerTextField()
                }
            }

            if (decorator == null) {
                bulletedTextField()
            } else {
                decorator.Decoration(bulletedTextField)
            }
        },
        scrollState = scrollState,
    )
}

/**
 * 각 줄(문단)의 시작 위치에 불릿을 그린다.
 *
 * - 줄의 시작은 텍스트 맨 앞, 그리고 `\n` 바로 뒤다.
 * - 줄바꿈으로 넘어간 줄에는 그리지 않는다. 불릿은 항목마다 하나다.
 * - 불릿은 문단 첫 글자 바로 왼쪽(들여쓰기 자리)에, 그 줄 상자(lineTop ~ lineBottom)의 세로 가운데에 맞춰 그린다.
 *     - 가로 위치는 문단 시작 offset의 커서 x에서 불릿 폭을 뺀 값이다. 커서 x에는 들여쓰기가 반영되어 있어, 들여쓰기 폭이 바뀌어도 불릿이 따라간다.
 *     - 들여쓰기 폭을 px → sp → px로 바꾸며 생기는 반올림 오차도 여기서 흡수된다.
 *     - 글자 기준선(baseline)에 맞추면, 한글처럼 대체 글꼴이 섞일 때 기준선이 움직여 불릿도 따라 움직인다.
 *     - 줄 상자는 글꼴과 상관없이 정해지므로 불릿이 움직이지 않는다. 단, [textStyle]에 `lineHeight`가 있고 `LineHeightStyle.Trim.None`이어야 모든 줄 상자 높이가 같다.
 */
private fun DrawScope.drawBullets(
    layout: TextLayoutResult,
    bulletLayout: TextLayoutResult,
    scroll: Offset,
) {
    val text = layout.layoutInput.text
    var lineStart = 0
    while (true) {
        val line = layout.getLineForOffset(lineStart) // lineStart가 속한 줄 번호
        val lineHeight = layout.getLineBottom(line) - layout.getLineTop(line) // 줄 상자의 아래쪽 Y - 위쪽 Y

        val centeringOffset = (lineHeight - bulletLayout.size.height) / 2
        val topOffset = layout.getLineTop(line) + centeringOffset // 불릿 상자를 줄 상자 가운데에 놓는다
        val leftOffset = layout.getHorizontalPosition(lineStart, usePrimaryDirection = true) - bulletLayout.size.width // 문단 첫 글자 바로 왼쪽
        drawText(bulletLayout, topLeft = Offset(leftOffset, topOffset) + scroll)

        val newline = text.indexOf('\n', startIndex = lineStart) // 다음 개행 문자의 인덱스. 없으면 -1
        if (newline < 0) break
        lineStart = newline + 1 // 개행 문자 바로 뒤에서 다음 줄 시작
    }
}
