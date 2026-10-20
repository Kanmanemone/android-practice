package com.example.swemobulletedlist.entry.optionstatepillbullet

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
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Density

/** 각 줄 앞에 그리는 Bullet (글머리 기호). 뒤 간격까지 포함한다. */
private const val BULLET = "•  "

/** 선택이 문단 끝 `\n`을 넘어갈 때 그 줄 끝에 덧칠하는 폭의 기준 글자. 빈 문단도 선택된 게 보이게 한다. */
private const val NEWLINE_MARK = " "

/**
 * 단일 [BasicTextField] 하나로 그리는 불릿 리스트 입력 필드.
 *
 * - 한 줄이 곧 한 항목이다. 개행으로 항목을 나누고, 줄 맨 앞 Backspace로 합친다.
 * - 불릿은 텍스트가 아니라 그림이다. 모든 문단을 [BULLET] 폭만큼 들여쓰고(`textIndent`), 그 들여쓰기 자리에 불릿을 그린다.
 *     - 화면 텍스트가 저장값과 똑같아서, 커서 위치를 따로 보정할 필요가 없다.
 *     - 첫 줄(`firstLine`)과 넘친 줄(`restLine`)을 같은 폭으로 들여쓰므로, 긴 줄이 넘어가도 글자 시작선에 맞춰진다.
 *     - [0] drawn-bullet은 이 자리를 decorator의 padding으로 냈다. 들여쓰기는 문단마다 다르게 줄 수 있어서 그쪽으로 바꿨다.
 * - 들여쓰기는 문단마다 따로 건다([bulletListOutputTransformation]). 불릿은 문단 첫 글자를 따라 그리므로 들여쓰기가 바뀌면 같이 움직인다.
 *     - 지금은 확인용 규칙 하나만 있다. `>`로 시작하는 문단은 들여쓰기가 2배다.
 * - 선택 하이라이트도 직접 그린다. 기본 하이라이트는 가운데 줄을 줄 전체 폭으로 칠해 불릿을 덮기 때문이다.
 *     - 기본 하이라이트는 `LocalTextSelectionColors`의 `backgroundColor`를 투명으로 바꿔 끈다. 손잡이 색은 그대로 둔다.
 *     - 칠하는 색은 호출부의 `LocalTextSelectionColors`에서 가져온다. 테마의 선택 색이 그대로 쓰인다.
 * - 파라미터는 `BasicTextField(state)`와 같다. 단, `inputTransformation`과 `outputTransformation`은 없다.
 *     - `inputTransformation`은 sentinel 규칙(`BulletListTransformation.kt`)이 차지한다.
 *     - `outputTransformation`은 문단별 들여쓰기(`BulletListTransformation.kt`)가 차지한다.
 * - [state]는 [rememberBulletListTextFieldState]로 만든 것이어야 한다. 맨 앞 sentinel을 전제로 동작하기 때문이다.
 * - 기본값도 `BasicTextField`와 같다. 테마 색(다크모드 등)이 필요하면 호출부가 [textStyle], [cursorBrush]로 넘긴다.
 * - [textStyle]의 `textIndent`는 무시된다. 문단마다 불릿 자리를 내는 들여쓰기로 덮어쓰기 때문이다.
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
     * 일반 문단의 들여쓰기. 폭은 불릿 한 개의 폭이다.
     *
     * - `TextIndent`는 sp 단위만 받으므로, 측정한 px 폭을 sp로 바꿔 넣는다.
     * - [textStyle]에도 기본값으로 넣는다. 문단별 스타일이 빠진 곳이 있어도 불릿 자리는 남는다.
     */
    val bulletIndent = with(LocalDensity.current) { bulletLayout.size.width.toSp() }
    val defaultIndent = TextIndent(firstLine = bulletIndent, restLine = bulletIndent)
    val indentedTextStyle = remember(textStyle, defaultIndent) { textStyle.copy(textIndent = defaultIndent) }

    /**
     * 문단마다 들여쓰기를 거는 [bulletListOutputTransformation].
     *
     * - 확인용 규칙: `>`로 시작하는 문단은 들여쓰기가 2배다.
     */
    val outputTransformation = remember(bulletIndent) {
        val quoteIndent = TextIndent(firstLine = bulletIndent * 2, restLine = bulletIndent * 2)
        bulletListOutputTransformation { text, start, end ->
            if (text.paragraphFirstChar(start, end) == '>') quoteIndent else defaultIndent
        }
    }

    /** 문단 끝 `\n`까지 선택했을 때 줄 끝에 덧칠하는 폭. */
    val newlineMarkWidth = remember(textMeasurer, textStyle) { textMeasurer.measure(NEWLINE_MARK, textStyle).size.width.toFloat() }

    /**
     * 선택 색.
     *
     * - [selectionColor]는 직접 칠할 때 쓰고, 기본 하이라이트는 [hiddenHighlightColors]로 투명하게 만든다.
     */
    val selectionColors = LocalTextSelectionColors.current
    val selectionColor = selectionColors.backgroundColor
    val hiddenHighlightColors = remember(selectionColors) {
        TextSelectionColors(handleColor = selectionColors.handleColor, backgroundColor = Color.Transparent)
    }

    var getTextLayout: (() -> TextLayoutResult?)? by remember { mutableStateOf(null) }

    CompositionLocalProvider(LocalTextSelectionColors provides hiddenHighlightColors) {
        BasicTextField(
            state = state,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            inputTransformation = BulletListInputTransformation,
            outputTransformation = outputTransformation,
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
                 * 텍스트 영역 뒤에 선택 하이라이트와 불릿을 그린다.
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
                                    drawSelection(layout, state.selection, selectionColor, newlineMarkWidth, scroll)
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
}

/**
 * 각 문단의 시작 위치에 불릿을 그린다.
 *
 * - 문단은 layout에 걸린 문단 스타일 범위([paragraphRanges])로 찾는다. 화면용 텍스트에는 `\n`이 없기 때문이다.
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
    for (paragraph in layout.paragraphRanges) {
        val line = layout.getLineForOffset(paragraph.start) // 문단 첫 줄 번호
        val lineHeight = layout.getLineBottom(line) - layout.getLineTop(line) // 줄 상자의 아래쪽 Y - 위쪽 Y

        val centeringOffset = (lineHeight - bulletLayout.size.height) / 2
        val topOffset = layout.getLineTop(line) + centeringOffset // 불릿 상자를 줄 상자 가운데에 놓는다
        val leftOffset = layout.getHorizontalPosition(paragraph.start, usePrimaryDirection = true) - bulletLayout.size.width // 문단 첫 글자 바로 왼쪽
        drawText(bulletLayout, topLeft = Offset(leftOffset, topOffset) + scroll)
    }
}

/**
 * 선택 범위를 줄마다 직사각형으로 칠한다. 기본 하이라이트를 대신한다.
 *
 * - 줄마다 칠하는 가로 범위는 이렇다.
 *     - 시작 x: 첫 줄이면 선택 시작 offset의 커서 x, 아니면 그 줄 첫 글자의 커서 x.
 *     - 끝 x: 마지막 줄이면 선택 끝 offset의 커서 x, 아니면 `getLineRight`(줄 끝 공백 제외).
 * - 줄 첫 글자의 커서 x에는 들여쓰기가 반영되어 있어, 불릿 자리는 칠하지 않는다.
 *     - `getLineLeft`는 쓰지 않는다. Android 레이아웃은 왼쪽 정렬 줄에서 들여쓰기와 상관없이 0을 돌려준다.
 * - 선택이 문단 끝을 넘어가면 그 줄 끝에 [newlineMarkWidth]만큼 더 칠한다. 빈 문단도 선택된 게 보인다.
 * - 세로 범위는 줄 상자(lineTop ~ lineBottom)다.
 * - 왼쪽에서 오른쪽으로 쓰는 글(LTR)만 고려한다.
 */
private fun DrawScope.drawSelection(
    layout: TextLayoutResult,
    selection: TextRange,
    color: Color,
    newlineMarkWidth: Float,
    scroll: Offset,
) {
    if (selection.collapsed) return
    val paragraphEnds = layout.paragraphRanges.mapTo(HashSet()) { it.end }
    val firstLine = layout.getLineForOffset(selection.min)
    val lastLine = layout.getLineForOffset(selection.max)
    for (line in firstLine..lastLine) {
        val left = if (line == firstLine) {
            layout.getHorizontalPosition(selection.min, usePrimaryDirection = true)
        } else {
            layout.getHorizontalPosition(layout.getLineStart(line), usePrimaryDirection = true)
        }
        val right = if (line == lastLine) {
            layout.getHorizontalPosition(selection.max, usePrimaryDirection = true)
        } else {
            val lineEnd = layout.getLineEnd(line) // 문단을 끝내는 줄이면 `\n` 자리 바로 뒤
            if (lineEnd in paragraphEnds) {
                /** 줄 끝 공백이 `getLineRight`에 들어가는지는 Android 버전마다 달라, `\n` 자리 x를 기준으로 덧칠한다. */
                maxOf(layout.getLineRight(line), layout.getHorizontalPosition(lineEnd - 1, usePrimaryDirection = true) + newlineMarkWidth)
            } else {
                layout.getLineRight(line)
            }
        }
        if (right <= left) continue

        val top = layout.getLineTop(line)
        drawRect(
            color = color,
            topLeft = Offset(left, top) + scroll,
            size = Size(right - left, layout.getLineBottom(line) - top),
        )
    }
}

/**
 * layout에 걸린 문단 스타일 범위. 곧 문단 목록이다.
 *
 * - [bulletListOutputTransformation]이 모든 문단에 빠짐없이 걸기 때문이다.
 * - 저장값이 아니라 layout에서 읽어, 그리는 시점의 layout과 어긋나지 않는다.
 */
private val TextLayoutResult.paragraphRanges
    get() = layoutInput.text.paragraphStyles
