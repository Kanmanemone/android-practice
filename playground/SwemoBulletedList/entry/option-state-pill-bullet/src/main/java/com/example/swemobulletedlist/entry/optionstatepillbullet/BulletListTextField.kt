package com.example.swemobulletedlist.entry.optionstatepillbullet

import android.util.LruCache
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
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

/** 각 줄 앞에 그리는 Bullet (글머리 기호). 뒤 간격까지 포함한다. */
private const val BULLET = "•  "

/** 선택이 문단 끝 `\n`을 넘어갈 때 그 줄 끝에 덧칠하는 폭의 기준 글자. 빈 문단도 선택된 게 보이게 한다. */
private const val NEWLINE_MARK = " "

/** 알약 `:` 가로 배율의 하한. 0 이하면 폭이 없거나 뒤집힌다. */
private const val MIN_PILL_SCALE_X = 0.01f

/**
 * 알약 배치를 잴 때 구분자 공백 뒤에 두는 본문 글자.
 *
 * - 공백이 줄 끝이 아니게 만들 뿐이라 어떤 글자든 된다.
 */
private const val PILL_BODY_PROBE = "x"

/** [fitScaleX]가 배율을 재 보고 고치는 최대 횟수. */
private const val PILL_SCALE_FIT_ITERATIONS = 4

/** [fitScaleX]가 맞았다고 보는 폭 차이(px). */
private const val PILL_SCALE_FIT_EPSILON = 0.01f

/** 라벨별 [PillLayout]을 기억해 두는 개수. 화면에 동시에 보이는 알약 라벨 수보다 넉넉하면 된다. */
private const val PILL_LAYOUT_CACHE_SIZE = 64

/** 알약 배경을 줄 상자보다 위아래로 줄이는 폭. 위아래 줄의 알약끼리 붙어 보이지 않게 한다. */
private val PILL_VERTICAL_INSET = 2.dp

/**
 * 단일 [BasicTextField] 하나로 그리는 불릿 리스트 입력 필드.
 *
 * - 한 줄이 곧 한 항목이다. 개행으로 항목을 나누고, 줄 맨 앞 Backspace로 합친다.
 * - 불릿은 텍스트가 아니라 그림이다. 모든 문단을 [BULLET] 폭만큼 들여쓰고(`textIndent`), 그 들여쓰기 자리에 불릿을 그린다.
 *     - 화면 텍스트가 저장값과 똑같아서, 커서 위치를 따로 보정할 필요가 없다.
 *     - 첫 줄(`firstLine`)과 넘친 줄(`restLine`)을 같은 폭으로 들여쓰므로, 긴 줄이 넘어가도 글자 시작선에 맞춰진다.
 *     - [0] drawn-bullet은 이 자리를 decorator의 padding으로 냈다. 들여쓰기는 문단마다 다르게 줄 수 있어서 그쪽으로 바꿨다.
 * - 들여쓰기는 문단마다 따로 건다([bulletListOutputTransformation]). 불릿은 문단 첫 글자를 따라 그리므로 들여쓰기가 바뀌면 같이 움직인다.
 * - 문단 맨 앞이 `라벨: `이면 라벨을 알약으로 보여 준다([pillRangeOf]).
 *     - 글자는 그대로 두고, 구분자 `: `만 투명하게 칠한 뒤 `라벨:` 뒤에 둥근 배경을 그린다.
 *     - 알약 문단에는 불릿을 그리지 않는다. 알약이 불릿 자리에서 시작해 구분자 역할을 한다.
 *     - 공백을 쳐서 알약이 생겨도 커서는 움직이지 않는다. 본문 시작 x를 일반 문단일 때와 같게 맞춘다.
 *     - 알약은 x = 0(불릿 자리 시작)부터 투명한 `:` 뒤까지다. 구분자 공백은 알약과 본문 사이 간격으로 남긴다.
 *     - 라벨 글자는 알약 가운데에 온다. 왼쪽 여백(첫 줄 들여쓰기)과 오른쪽 여백(투명한 `:`)의 폭을 같게 맞춘다.
 *     - 넘친 줄 들여쓰기(r)는 일반 문단 글자 시작선이나 알약 뒤 본문 시작선 중에서 고른다.
 *     - 알약 배경은 텍스트 영역 왼쪽 밖까지 넘칠 수 있다. 글자는 `BasicTextField` 안쪽에서 잘리므로 배경만 넘는다.
 *     - 색·r·넘침 폭은 [pillStyle]로 정한다.
 *     - `BasicTextField`는 `InlineTextContent`를 지원하지 않아 진짜 composable을 넣을 수 없다. 그래서 그린다.
 * - 선택 하이라이트도 직접 그린다. 기본 하이라이트는 가운데 줄을 줄 전체 폭으로 칠해 불릿을 덮기 때문이다.
 *     - 기본 하이라이트는 `LocalTextSelectionColors`의 `backgroundColor`를 투명으로 바꿔 끈다. 손잡이 색은 그대로 둔다.
 *     - 칠하는 색은 호출부의 `LocalTextSelectionColors`에서 가져온다. 테마의 선택 색이 그대로 쓰인다.
 * - 복사·잘라내기는 저장값을 클립보드에 넣는다([StoredTextClipboard]). 기본 동작은 화면용 텍스트를 넣어 문단이 합쳐진다.
 * - 파라미터는 `BasicTextField(state)`와 같다. 단, `inputTransformation`과 `outputTransformation`은 없다.
 *     - `inputTransformation`은 sentinel 규칙(`BulletListTransformation.kt`)과 클립보드용 편집 기록([StoredTextClipboard])이 차지한다.
 *     - `outputTransformation`은 문단별 들여쓰기(`BulletListTransformation.kt`)가 차지한다.
 * - [state]는 [rememberBulletListTextFieldState]로 만든 것이어야 한다. 맨 앞 sentinel을 전제로 동작하기 때문이다.
 * - 기본값도 `BasicTextField`와 같다. 테마 색(다크모드 등)이 필요하면 호출부가 [textStyle], [cursorBrush]로 넘긴다.
 * - [textStyle]의 `textIndent`는 무시된다. 문단마다 불릿 자리를 내는 들여쓰기로 덮어쓰기 때문이다.
 *
 * @param bulletIndent 일반 문단의 들여쓰기(g). 불릿은 이 자리의 오른쪽 끝(글자 바로 왼쪽)에 그린다.
 *     - `Dp.Unspecified`면 불릿([BULLET]) 폭이다.
 * @param pillStyle 알약 모양과 알약 문단의 넘친 줄 들여쓰기(r).
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
) {
    /** 불릿 한 개를 [textStyle]로 측정한 결과. 들여쓰기 폭과 그리기에 같이 쓴다. */
    val textMeasurer = rememberTextMeasurer()
    val bulletLayout = remember(textMeasurer, textStyle) { textMeasurer.measure(BULLET, textStyle) }

    val density = LocalDensity.current

    /**
     * 일반 문단의 들여쓰기(g). 기본은 불릿 한 개의 폭이다.
     *
     * - `TextIndent`는 sp 단위만 받으므로, px·dp 폭을 sp로 바꿔 넣는다.
     * - [textStyle]에도 기본값으로 넣는다. 문단별 스타일이 빠진 곳이 있어도 불릿 자리는 남는다.
     */
    val bulletIndentPx = with(density) {
        if (bulletIndent == Dp.Unspecified) bulletLayout.size.width.toFloat() else bulletIndent.toPx()
    }
    val bulletIndentSp = with(density) { bulletIndentPx.toSp() }
    val defaultIndent = TextIndent(firstLine = bulletIndentSp, restLine = bulletIndentSp)
    val indentedTextStyle = remember(textStyle, defaultIndent) { textStyle.copy(textIndent = defaultIndent) }

    /**
     * 라벨별 [PillLayout]. 라벨마다 한 번 재서 기억한다([measurePillLayout]).
     *
     * - OutputTransformation은 글자를 칠 때마다 모든 문단에 돈다. 같은 라벨을 매번 다시 재지 않게 한다.
     * - 재는 기준(글꼴·g·넘침 폭)이 바뀌면 통째로 새로 만든다.
     */
    val pillLayouts = remember(textMeasurer, textStyle, bulletIndentPx, pillStyle.overflow, density) {
        val overflowPx = with(density) { pillStyle.overflow.toPx() }
        val toAppliedIndent = { px: Float -> density.toAppliedIndent(px) }
        val appliedBulletIndent = toAppliedIndent(bulletIndentPx)
        object : LruCache<String, PillLayout>(PILL_LAYOUT_CACHE_SIZE) {
            override fun create(label: String) =
                measurePillLayout(label, textMeasurer, textStyle, appliedBulletIndent, overflowPx, toAppliedIndent)
        }
    }

    /**
     * 문단마다 들여쓰기를 거는 [bulletListOutputTransformation].
     *
     * - 알약 문단은 [pillLayouts]에서 첫 줄 들여쓰기와 `:` 배율을 가져온다.
     * - 알약 문단의 넘친 줄(r)은 [PillStyle.restLineIndent]를 따른다.
     * - 나머지 문단은 g만큼 들여쓴다.
     */
    val outputTransformation = remember(defaultIndent, pillLayouts, pillStyle.restLineIndent, density) {
        bulletListOutputTransformation { text, start, end ->
            val pill = text.pillRangeOf(start, end) ?: return@bulletListOutputTransformation ParagraphLayout(defaultIndent)
            val layout = pillLayouts[text.substring(pill.labelStart, pill.labelEnd)]
            val indent = with(density) {
                TextIndent(
                    firstLine = layout.firstLineIndent.toSp(),
                    restLine = when (pillStyle.restLineIndent) {
                        PillRestLineIndent.Bullet -> bulletIndentSp
                        PillRestLineIndent.Pill -> layout.bodyStart.toSp()
                    },
                )
            }
            ParagraphLayout(indent, pillColonStyle = pillColonStyle(layout.colonScaleX))
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

    /** 복사·잘라내기 때 화면용 텍스트 대신 저장값을 넣는 클립보드. 문단 사이 `\n`이 공백으로 바뀌어 복사되지 않게 한다. */
    val clipboard = LocalClipboard.current
    val storedTextClipboard = remember(clipboard, state) { StoredTextClipboard(clipboard, state) }

    var getTextLayout: (() -> TextLayoutResult?)? by remember { mutableStateOf(null) }

    /**
     * layout 좌표를 텍스트 영역(아래 Box) 좌표로 바꾸는 스크롤 이동량.
     *
     * - 한 줄 모드는 가로로, 여러 줄 모드는 세로로 스크롤된다.
     */
    val currentScrollOffset = {
        val scrollOffset = scrollState.value.toFloat()
        if (lineLimits == TextFieldLineLimits.SingleLine) Offset(-scrollOffset, 0f) else Offset(0f, -scrollOffset)
    }

    /** 문단 오른쪽 빈 곳을 눌렀을 때 커서가 다음 문단 맨 앞으로 가지 않게 한다. */
    val paragraphEndTapFix = remember { ParagraphEndTapFix({ getTextLayout?.invoke() }, { currentScrollOffset() }) }

    /**
     * sentinel 규칙 뒤에 붙이는 단계들.
     *
     * - [ParagraphEndTapFix.inputTransformation]: 손가락으로 바뀐 선택을 고친다.
     * - [StoredTextClipboard.editRecorder]: 잘라내기 전 글을 클립보드가 알 수 있게 남긴다.
     */
    val inputTransformation = remember(storedTextClipboard, paragraphEndTapFix) {
        BulletListInputTransformation
            .then(paragraphEndTapFix.inputTransformation)
            .then(storedTextClipboard.editRecorder)
    }

    CompositionLocalProvider(
        LocalTextSelectionColors provides hiddenHighlightColors,
        LocalClipboard provides storedTextClipboard,
    ) {
        BasicTextField(
            state = state,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            inputTransformation = inputTransformation,
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
                 * 텍스트 영역 뒤에 알약 배경, 선택 하이라이트, 불릿을 차례로 그린다.
                 *
                 * - 선택 색은 반투명이라 알약 배경 위에 칠한다.
                 *
                 * - 호출부가 decorator를 넘겼다면, 이 Box가 그 decorator의 innerTextField 자리에 들어간다. 즉 불릿은 decorator 장식보다 안쪽이다.
                 * - Box와 텍스트 레이아웃의 좌표가 같다. 들여쓰기는 텍스트 레이아웃 안에서 일어나므로, 불릿 위치도 레이아웃 좌표로 바로 구한다.
                 */
                val bulletedTextField = @Composable {
                    Box(
                        Modifier
                            .then(paragraphEndTapFix.pointerModifier)
                            .drawWithContent {
                                /**
                                 * 그림을 Box 영역으로 자른다. 좌우만 알약 넘침 폭([PillStyle.overflow])만큼 넓힌다.
                                 *
                                 * - 위아래: 스크롤로 밀려난 불릿·알약이 영역 밖에 그려지지 않게 한다.
                                 * - 좌우: 알약 배경이 텍스트 영역 밖으로 넘칠 자리를 둔다.
                                 */
                                val overflow = pillStyle.overflow.toPx()
                                clipRect(left = -overflow, top = 0f, right = size.width + overflow, bottom = size.height) {
                                    this@drawWithContent.drawContent()
                                }
                            }
                            .drawBehind {
                                getTextLayout?.invoke()?.let { layout ->
                                    val scroll = currentScrollOffset()
                                    drawPills(layout, pillStyle.color, pillStyle.overflow.toPx(), scroll)
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
 * - 알약 문단에는 그리지 않는다. 알약이 불릿 자리를 차지한다.
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
    for (paragraph in layout.paragraphRanges) {
        if (text.pillRangeOf(paragraph.start, layout.paragraphContentEnd(paragraph)) != null) continue

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
 * 알약 문단마다 `라벨:` 뒤에 둥근 배경을 그린다.
 *
 * - 알약은 layout의 화면 텍스트에서 [pillRangeOf]로 다시 찾는다. 저장값과 offset이 같아 결과도 같다.
 * - 가로: x = 0(불릿 자리 시작)부터 `:` 뒤까지. 투명한 `:`가 오른쪽 안쪽 여백이다.
 *     - 첫 줄은 왼쪽으로 [overflow]만큼 더 키운다. 텍스트 영역 밖으로 넘친다.
 *     - 구분자 공백은 덮지 않는다. 알약과 본문 사이에 한 칸 간격이 남는다.
 * - 세로: 줄 상자에서 위아래로 [PILL_VERTICAL_INSET]만큼 줄인 높이. 모서리는 반원이다.
 * - 라벨이 줄을 넘치면 줄마다 따로 그린다.
 */
private fun DrawScope.drawPills(
    layout: TextLayoutResult,
    color: Color,
    overflow: Float,
    scroll: Offset,
) {
    val text = layout.layoutInput.text
    val inset = PILL_VERTICAL_INSET.toPx()
    for (paragraph in layout.paragraphRanges) {
        val pill = text.pillRangeOf(paragraph.start, layout.paragraphContentEnd(paragraph)) ?: continue

        val firstLine = layout.getLineForOffset(pill.labelStart)
        val lastLine = layout.getLineForOffset(pill.labelEnd) // `:`가 있는 줄
        for (line in firstLine..lastLine) {
            val left = if (line == firstLine) {
                -overflow
            } else {
                layout.getHorizontalPosition(layout.getLineStart(line), usePrimaryDirection = true)
            }
            val right = if (line == lastLine) {
                layout.getHorizontalPosition(pill.backgroundEnd, usePrimaryDirection = true)
            } else {
                layout.getLineRight(line)
            }
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

/**
 * layout에 걸린 문단 스타일 범위. 곧 문단 목록이다.
 *
 * - [bulletListOutputTransformation]이 모든 문단에 빠짐없이 걸기 때문이다.
 * - 저장값이 아니라 layout에서 읽어, 그리는 시점의 layout과 어긋나지 않는다.
 */
private val TextLayoutResult.paragraphRanges
    get() = layoutInput.text.paragraphStyles

/**
 * 문단 스타일 범위에서 문단 글자가 끝나는 위치.
 *
 * - 마지막 문단이 아니면 범위 끝에 `\n` 대신 넣은 문자가 있어 하나 뺀다.
 */
private fun TextLayoutResult.paragraphContentEnd(paragraph: AnnotatedString.Range<*>): Int =
    if (paragraph.end < layoutInput.text.length) paragraph.end - 1 else paragraph.end

/**
 * 알약 문단 앞부분(`라벨: `)을 화면과 같은 스타일로 만든다. 들여쓰기를 미리 잴 때 쓴다.
 *
 * - [colonStyle]은 구분자의 `:`에만 건다. 화면([bulletListOutputTransformation])과 같다.
 */
private fun pillText(label: String, colonStyle: SpanStyle) = buildAnnotatedString {
    append(label)
    withStyle(colonStyle) { append(PILL_DELIMITER.take(1)) }
    append(PILL_DELIMITER.drop(1))
}

/**
 * 알약 문단에서 구분자의 `:`에 거는 스타일.
 *
 * - 투명하게 칠해 숨긴다. 투명한 `:`가 알약 오른쪽 안쪽 여백이 된다.
 * - 폭은 가로 배율([scaleX])로 맞춘다.
 *     - 자간(`letterSpacing`)은 쓰지 않는다. Android는 자간을 글자 앞뒤로 반씩 나눠 붙여, 절반가량이 뒤 공백 폭으로 새어 나간다.
 *     - 가로 배율은 그 글자의 폭에 배율을 곱할 뿐이라, 다른 글자에 영향이 없다.
 */
private fun pillColonStyle(scaleX: Float) =
    SpanStyle(color = Color.Transparent, textGeometricTransform = TextGeometricTransform(scaleX = scaleX))

/**
 * 알약 문단 앞부분(`라벨: `)의 배치. 값은 문단 왼쪽 끝(불릿 자리 시작)이 0인 px이다.
 *
 * @property firstLineIndent 첫 줄 들여쓰기로 넘길 값. Android가 정수로 올려 쓰며, 그 결과가 라벨 글자의 시작 x다.
 * @property bodyStart 본문 시작 x. 일반 문단일 때 `라벨: ` 뒤 커서가 있던 자리와 같다.
 * @property colonScaleX 투명한 `:`의 가로 배율([pillColonStyle]).
 */
private class PillLayout(val firstLineIndent: Float, val bodyStart: Float, val colonScaleX: Float)

/**
 * [label]의 알약 배치를 잰다.
 *
 * - 두 가지를 맞춘다. 앞의 것이 우선이다.
 *     1. 본문 시작 = g + `라벨: ` 폭. 공백을 쳐서 알약이 생겨도 커서가 움직이지 않는다. 정확히 맞춘다.
 *     2. 왼쪽 여백 = 오른쪽 여백. 라벨이 알약 가운데에 온다. 1px 안쪽으로 맞춘다.
 *         - 왼쪽 여백 = 넘침 폭 + 첫 줄 들여쓰기. 알약 배경은 왼쪽으로 넘침 폭만큼 더 그려진다.
 *         - 오른쪽 여백 = 투명한 `:`의 폭.
 * - 정할 값은 첫 줄 들여쓰기 p와 `:` 폭이다. 본문 시작 = p + 라벨 폭 + `:` 폭 + 공백 폭이다.
 *     1. p = (본문 시작 - 라벨 폭 - 공백 폭 - 넘침 폭) / 2. 두 조건을 함께 풀어 나온다.
 *         - Android는 들여쓰기를 정수 px로 올려 쓴다. 그래서 목표에 가장 가까운 정수 n을 고르고, n - 0.5를 넘겨 n으로 올라가게 한다.
 *         - 정수 n을 그대로 넘기면 sp를 거치는 변환 오차(특히 비선형 글꼴 배율)로 n보다 조금 커져 n + 1로 올라갈 수 있다.
 *         - 이어지는 계산은 실제로 쓰일 값([toAppliedIndent])으로 한다.
 *         - 이 반올림 때문에 조건 2가 1px 안쪽으로 어긋날 수 있다.
 *     2. `:` 폭 = 본문 시작 - p - 라벨 폭 - 공백 폭. 조건 1이다.
 * - `:` 폭은 가로 배율로 맞추고, 배율은 재 보면서 고친다([fitScaleX]).
 *     - 공백은 가로 배율이 먹지 않아 원래 폭 그대로 둔다.
 * - 넘침 폭이 너무 커서 p가 0보다 작으면, p를 0으로 둔다. 조건 1만 맞고 라벨은 오른쪽으로 치우친다.
 * - 들여쓰기는 layout 전에 정해야 해서 layout 결과를 쓸 수 없다. 그래서 [textStyle]로 미리 잰다.
 *     - 화면과 같은 모양으로 잰다. `:`에 스타일을 걸어 화면과 같은 자리에서 글자 묶음(run)이 나뉘게 하고, 공백 뒤에 본문 글자를 하나 둔다.
 *     - 공백이 줄 끝이면 Android가 자간을 덜 붙여, 본문이 있을 때와 폭이 다르다. 화면에 오래 남는 쪽(본문이 있을 때)에 맞춘다.
 *
 * @param bulletIndent 일반 문단 들여쓰기(g). Android가 실제로 쓰는 정수 값이어야 한다.
 * @param toAppliedIndent 들여쓰기로 넘긴 px를 Android가 실제로 쓰는 px로 바꾼다.
 */
private fun measurePillLayout(
    label: String,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    bulletIndent: Float,
    overflow: Float,
    toAppliedIndent: (Float) -> Float,
): PillLayout {
    val colonOffset = label.length
    val bodyOffset = label.length + PILL_DELIMITER.length
    fun TextLayoutResult.x(offset: Int) = getHorizontalPosition(offset, usePrimaryDirection = true)
    fun measure(colonScaleX: Float) = textMeasurer.measure(
        buildAnnotatedString {
            append(pillText(label, pillColonStyle(colonScaleX)))
            append(PILL_BODY_PROBE)
        },
        textStyle,
    )

    val bodyStart = bulletIndent + textMeasurer.measure(label + PILL_DELIMITER + PILL_BODY_PROBE, textStyle).x(bodyOffset)
    val unscaled = measure(1f)
    val labelWidth = unscaled.x(colonOffset)
    val spaceWidth = unscaled.x(bodyOffset) - unscaled.x(colonOffset + 1)

    val targetIndent = (bodyStart - labelWidth - spaceWidth - overflow).coerceAtLeast(0f) / 2
    val requestedIndent = (targetIndent.roundToInt() - 0.5f).coerceAtLeast(0f) // 올려 쓰이므로 반 px 아래를 넘긴다
    val indent = toAppliedIndent(requestedIndent) // p

    val colonScaleX = fitScaleX(bodyStart - indent - labelWidth - spaceWidth) { scaleX ->
        measure(scaleX).let { it.x(colonOffset + 1) - it.x(colonOffset) }
    }
    return PillLayout(requestedIndent, bodyStart, colonScaleX)
}

/**
 * 글자 폭이 [target]에 가장 가까워지는 가로 배율을 찾는다.
 *
 * - 배율 1에서 시작해, 잰 폭과 목표의 비율만큼 배율을 고치기를 몇 번 되풀이한다.
 *     - 가로 배율은 폭에 거의 비례하므로 보통 한두 번이면 맞는다.
 *     - 글자 폭을 정수 px로 그리면 정확히 맞지 않을 수 있다. 그래서 되풀이 중 가장 가까웠던 배율을 고른다.
 *
 * @param widthAt 배율을 받아 그 배율로 그린 글자 폭을 잰다.
 */
private inline fun fitScaleX(target: Float, widthAt: (scaleX: Float) -> Float): Float {
    var scaleX = 1f
    var bestScaleX = 1f
    var bestError = Float.MAX_VALUE
    repeat(PILL_SCALE_FIT_ITERATIONS) {
        val width = widthAt(scaleX)
        val error = abs(width - target)
        if (error < bestError) {
            bestScaleX = scaleX
            bestError = error
        }
        if (error < PILL_SCALE_FIT_EPSILON || width <= 0f) return bestScaleX
        scaleX = (scaleX * target / width).coerceAtLeast(MIN_PILL_SCALE_X)
    }
    return bestScaleX
}

/**
 * 들여쓰기로 넘긴 [px]를 Android가 실제로 쓰는 px로 바꾼다.
 *
 * - Compose는 `TextIndent`(sp)를 px로 바꾼 뒤 정수로 올려(`ceil`) Android에 넘긴다. 그 과정을 똑같이 따른다.
 * - 같은 [Density]로 sp를 거쳐 계산해, 변환 중 생기는 작은 오차까지 실제와 같게 나온다.
 */
private fun Density.toAppliedIndent(px: Float): Float = ceil(px.toSp().toPx())

/** 텍스트 맨 끝 offset의 커서 x. `size.width`와 달리 끝 공백 폭까지 들어간다. */
private fun TextLayoutResult.endX(): Float =
    getHorizontalPosition(layoutInput.text.length, usePrimaryDirection = true)
