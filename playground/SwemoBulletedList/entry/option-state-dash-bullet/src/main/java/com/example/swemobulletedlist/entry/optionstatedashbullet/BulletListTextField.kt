package com.example.swemobulletedlist.entry.optionstatedashbullet

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
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 선택이 문단 끝 `\n`을 넘어갈 때 그 줄 끝에 덧칠하는 폭의 기준 글자. 빈 문단도 선택된 게 보이게 한다. */
private const val NEWLINE_MARK = " "

/** 알약 배경을 줄 상자보다 위아래로 줄이는 폭. 위아래 줄의 알약끼리 붙어 보이지 않게 한다. */
private val PILL_VERTICAL_INSET = 2.dp

/**
 * 단일 [BasicTextField] 하나로 그리는 불릿 리스트 입력 필드.
 *
 * - 한 줄이 곧 한 항목이다. 개행으로 항목을 나누고, 줄 맨 앞 Backspace로 합친다.
 * - 화면 텍스트의 글자와 줄 나눔은 저장값과 똑같다. 일반 `BasicTextField`처럼 텍스트 전체가 레이아웃 하나다.
 *     - 그래서 탭·드래그·선택 손잡이·복사·붙여넣기가 메모장과 똑같이 동작한다. 따로 보정하는 코드가 없다.
 *     - 화면용 처리([BulletListOutputTransformation])는 글자 색만 바꾸고, 끝이 `\n`이면 폭 0 문자를 하나 붙인다.
 * - 불릿은 텍스트가 아니라 그림이다. 모든 문단을 같은 폭(g)만큼 들여쓰고(`textIndent`), 그 들여쓰기 자리에 불릿을 그린다.
 *     - 불릿은 알약 배경색의 짧고 둥근 가로 막대(대시)다([dashStyle]). `-`보다 조금 길고 두껍다.
 *     - 들여쓰기는 [textStyle] 하나에 건다. Android는 `\n`으로 나뉜 문단마다 첫 줄을 들여쓴다.
 *     - 첫 줄(`firstLine`)과 넘친 줄(`restLine`)을 같은 폭으로 들여쓰므로, 긴 줄이 넘어가도 글자 시작선에 맞춰진다.
 *     - 문단마다 들여쓰기를 다르게 주지 않는다. 그러려면 문단마다 `ParagraphStyle`을 걸어야 하고, Compose가 문단마다 레이아웃을 따로 만들어 위 동작들이 어긋난다.
 * - 문단 맨 앞이 `라벨: `이면 라벨을 알약으로 보여 준다([pillRangeOf]).
 *     - 글자는 그대로 두고, 구분자의 `:`만 투명하게 칠한 뒤 라벨 둘레에 둥근 배경을 그린다.
 *     - 알약 문단에는 불릿을 그리지 않는다. 알약이 구분자 역할을 한다.
 *     - 알약은 라벨 앞뒤로 투명한 `:` 폭만큼 여백을 둔다. 오른쪽 여백이 투명한 `:` 자체라 좌우 여백이 늘 같고, 라벨이 가운데에 온다.
 *     - 구분자 공백은 덮지 않는다. 알약과 본문 사이 간격으로 남는다.
 *     - 레이아웃은 일반 문단과 똑같다. 공백을 쳐서 알약이 생겨도 글자도 커서도 움직이지 않는다.
 *     - `BasicTextField`는 `InlineTextContent`를 지원하지 않아 진짜 composable을 넣을 수 없다. 그래서 그린다.
 * - 선택 하이라이트도 직접 그린다. 기본 하이라이트는 가운데 줄을 줄 전체 폭으로 칠해 불릿을 덮기 때문이다.
 *     - 기본 하이라이트는 `LocalTextSelectionColors`의 `backgroundColor`를 투명으로 바꿔 끈다. 손잡이 색은 그대로 둔다.
 *     - 칠하는 색은 호출부의 `LocalTextSelectionColors`에서 가져온다. 테마의 선택 색이 그대로 쓰인다.
 * - 파라미터는 `BasicTextField(state)`와 같다. 단, `inputTransformation`과 `outputTransformation`은 없다.
 *     - `inputTransformation`은 sentinel 규칙(`BulletListTransformation.kt`)이 차지한다.
 *     - `outputTransformation`은 화면용 처리([BulletListOutputTransformation])가 차지한다.
 * - [state]는 [rememberBulletListTextFieldState]로 만든 것이어야 한다. 맨 앞 sentinel을 전제로 동작하기 때문이다.
 * - 기본값도 `BasicTextField`와 같다. 테마 색(다크모드 등)이 필요하면 호출부가 [textStyle], [cursorBrush]로 넘긴다.
 * - [textStyle]의 `textIndent`는 무시된다. 불릿 자리를 내는 들여쓰기로 덮어쓰기 때문이다.
 *
 * @param bulletIndent 문단 들여쓰기(g). 대시는 이 자리의 오른쪽 끝(글자 바로 왼쪽)에 [DashBulletStyle.gap]만큼 띄워 그린다.
 *     - `Dp.Unspecified`면 대시 길이 + 간격이다.
 * @param pillStyle 알약 모양. 대시도 알약 배경색으로 칠한다.
 * @param dashStyle 대시 불릿의 길이·두께·간격.
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
    bulletIndent: Dp = Dp.Unspecified,
    pillStyle: PillStyle = PillStyle(),
    dashStyle: DashBulletStyle = DashBulletStyle(),
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    /** 대시 불릿의 px 크기. 들여쓰기 폭과 그리기에 같이 쓴다. */
    val dash = remember(dashStyle, textStyle, density) { dashStyle.toMetrics(textStyle, density) }

    /**
     * 문단 들여쓰기(g)를 건 글자 스타일. g는 기본이 대시 길이 + 간격이다.
     *
     * - `TextIndent`는 sp 단위만 받으므로, px·dp 폭을 sp로 바꿔 넣는다.
     */
    val bulletIndentSp = with(density) {
        if (bulletIndent == Dp.Unspecified) dash.indent.toSp() else bulletIndent.toSp()
    }
    val indentedTextStyle = remember(textStyle, bulletIndentSp) {
        textStyle.copy(textIndent = TextIndent(firstLine = bulletIndentSp, restLine = bulletIndentSp))
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
                 * 텍스트 영역 뒤에 알약 배경, 선택 하이라이트, 불릿을 차례로 그린다.
                 *
                 * - 선택 색은 반투명이라 알약 배경 위에 칠한다.
                 * - 호출부가 decorator를 넘겼다면, 이 Box가 그 decorator의 innerTextField 자리에 들어간다. 즉 불릿은 decorator 장식보다 안쪽이다.
                 * - Box와 텍스트 레이아웃의 좌표가 같다. 들여쓰기는 텍스트 레이아웃 안에서 일어나므로, 불릿 위치도 레이아웃 좌표로 바로 구한다.
                 * - 그림은 Box 영역으로 자른다. 스크롤로 밀려난 불릿·알약이 영역 밖에 그려지지 않게 한다.
                 */
                val bulletedTextField = @Composable {
                    Box(
                        Modifier
                            .clipToBounds()
                            .drawBehind {
                                getTextLayout?.invoke()?.let { layout ->
                                    val scrollOffset = scrollState.value.toFloat()
                                    val scroll = if (lineLimits == TextFieldLineLimits.SingleLine) {
                                        Offset(-scrollOffset, 0f) // 한 줄 모드는 가로로 스크롤된다
                                    } else {
                                        Offset(0f, -scrollOffset) // 여러 줄 모드는 세로로 스크롤된다
                                    }
                                    drawPills(layout, pillStyle.color, scroll)
                                    drawSelection(layout, state.selection, selectionColor, newlineMarkWidth, scroll)
                                    drawDashBullets(layout, dash, pillStyle.color, scroll)
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
 * 각 문단의 시작 위치에 대시 불릿을 그린다.
 *
 * - 문단은 layout의 화면 텍스트를 `\n`으로 나눠 찾는다([forEachParagraph]).
 * - 줄바꿈으로 넘어간 줄에는 그리지 않는다. 불릿은 항목마다 하나다.
 * - 알약 문단에는 그리지 않는다. 알약이 구분자 역할을 한다.
 * - 대시는 양 끝이 둥근 가로 막대다. 문단 첫 글자 바로 왼쪽(들여쓰기 자리)에, 그 줄 상자(lineTop ~ lineBottom)의 세로 가운데에 맞춰 그린다.
 *     - 오른쪽 끝은 문단 시작 offset의 커서 x에서 [DashBulletMetrics.gap]을 뺀 자리다. 커서 x에는 들여쓰기가 반영되어 있어, 들여쓰기 폭이 바뀌어도 대시가 따라간다.
 *     - 들여쓰기 폭을 px → sp → px로 바꾸며 생기는 반올림 오차도 여기서 흡수된다.
 *     - 글자 기준선(baseline)에 맞추면, 한글처럼 대체 글꼴이 섞일 때 기준선이 움직여 대시도 따라 움직인다.
 *     - 줄 상자는 글꼴과 상관없이 정해지므로 대시가 움직이지 않는다. 단, `textStyle`에 `lineHeight`가 있고 `LineHeightStyle.Trim.None`이어야 모든 줄 상자 높이가 같다.
 */
private fun DrawScope.drawDashBullets(
    layout: TextLayoutResult,
    dash: DashBulletMetrics,
    color: Color,
    scroll: Offset,
) {
    val text = layout.layoutInput.text
    forEachParagraph(text) { start, end ->
        if (text.pillRangeOf(start, end) != null) return@forEachParagraph

        val line = layout.getLineForOffset(start) // 문단 첫 줄 번호
        val centerY = (layout.getLineTop(line) + layout.getLineBottom(line)) / 2 // 줄 상자의 세로 가운데
        val right = layout.getHorizontalPosition(start, usePrimaryDirection = true) - dash.gap // 문단 첫 글자에서 간격만큼 왼쪽
        drawRoundRect(
            color = color,
            topLeft = Offset(right - dash.length, centerY - dash.thickness / 2) + scroll,
            size = Size(dash.length, dash.thickness),
            cornerRadius = CornerRadius(dash.thickness / 2),
        )
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
    val text = layout.layoutInput.text
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
            val lineEnd = layout.getLineEnd(line) // 문단을 끝내는 줄이면 `\n` 바로 뒤
            if (lineEnd > 0 && text[lineEnd - 1] == '\n') {
                /** 줄 끝 `\n`이 `getLineRight`에 들어가는지는 Android 버전마다 달라, `\n` 자리 x를 기준으로 덧칠한다. */
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
 * 알약 문단마다 라벨 둘레에 둥근 배경을 그린다.
 *
 * - 알약은 layout의 화면 텍스트에서 [pillRangeOf]로 다시 찾는다. 저장값과 offset이 같아 결과도 같다.
 * - 가로: 라벨 앞뒤로 투명한 `:` 폭(c)만큼 여백을 둔다. 라벨 시작 x - c부터 `:` 뒤까지다.
 *     - 오른쪽 여백은 투명한 `:` 자체다. 왼쪽 여백도 같은 폭이라 라벨이 가운데에 온다.
 *     - 구분자 공백은 덮지 않는다. 알약과 본문 사이에 한 칸 간격이 남는다.
 * - 세로: 줄 상자에서 위아래로 [PILL_VERTICAL_INSET]만큼 줄인 높이. 모서리는 반원이다.
 * - 라벨이 줄을 넘치면 줄마다 따로 그린다.
 */
private fun DrawScope.drawPills(
    layout: TextLayoutResult,
    color: Color,
    scroll: Offset,
) {
    val text = layout.layoutInput.text
    val inset = PILL_VERTICAL_INSET.toPx()
    forEachParagraph(text) { start, end ->
        val pill = text.pillRangeOf(start, end) ?: return@forEachParagraph

        val colonRight = layout.getHorizontalPosition(pill.backgroundEnd, usePrimaryDirection = true)
        val padding = colonRight - layout.getHorizontalPosition(pill.labelEnd, usePrimaryDirection = true) // 투명한 `:` 폭
        val firstLine = layout.getLineForOffset(pill.labelStart)
        val lastLine = layout.getLineForOffset(pill.labelEnd) // `:`가 있는 줄
        for (line in firstLine..lastLine) {
            val left = if (line == firstLine) {
                layout.getHorizontalPosition(pill.labelStart, usePrimaryDirection = true) - padding
            } else {
                layout.getHorizontalPosition(layout.getLineStart(line), usePrimaryDirection = true)
            }
            val right = if (line == lastLine) colonRight else layout.getLineRight(line)
            val top = layout.getLineTop(line) + inset
            val height = layout.getLineBottom(line) - inset - top
            drawRoundRect(
                color = color,
                topLeft = Offset(left, top) + scroll,
                size = Size(right - left, height),
                cornerRadius = CornerRadius(height / 2),
            )
        }
    }
}
