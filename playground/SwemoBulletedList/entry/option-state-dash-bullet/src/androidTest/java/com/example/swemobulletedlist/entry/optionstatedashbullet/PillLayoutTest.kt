package com.example.swemobulletedlist.entry.optionstatedashbullet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.math.roundToInt

private const val FIELD_TAG = "bulletListTextField"

/** 알약 라벨들. 글자 폭이 고른 것·좁은 것·넓은 것·한글·공백이 섞인 것을 고루 넣는다. */
private val LABELS = listOf("abc", "iii", "WWW", "가나다", "Label 1", "x")

/** 일반 문단. */
private const val PLAIN_PARAGRAPH = "plain"

/** 맨 앞 sentinel([rememberBulletListTextFieldState]가 붙이는 문자)을 포함한 저장값. */
private val STORED_TEXT = "​" + (LABELS.map { "$it: body" } + PLAIN_PARAGRAPH).joinToString("\n")

/** 알약 색. 배경·글자와 헷갈리지 않는 색으로 둔다. */
private val PILL_COLOR = Color(0xFFFF0000)

/** 글자 위치 비교 허용 오차(px). 부동소수 오차만 허용한다. */
private const val POSITION_TOLERANCE_PX = 0.01f

/**
 * 구분자(`: `) 자리와 알약 문단 줄 끝의 위치 허용 오차(px).
 *
 * - 투명한 `:`에 색 스타일을 걸면 Android가 그 경계에서 글자 묶음(run)을 나눈다. 자간이 있는 글자 스타일에서는 묶음 경계의 자간이 조금 달라진다.
 * - 실기기에서 0.9px였다. `:`는 투명해서 보이지 않고, 라벨·본문 글자 위치는 그대로다.
 */
private const val DELIMITER_TOLERANCE_PX = 1f

/** 그려진 알약의 좌우 여백 차이 허용 오차(px). 픽셀 경계와 안티에일리어싱만 허용한다. */
private const val DRAWN_PADDING_TOLERANCE_PX = 1.5f

/**
 * 확인할 글자 스타일.
 *
 * - 자간이 없는 스타일: 글자 위치가 정수 px로 떨어진다.
 * - 자간이 있는 스타일: Material3 bodyLarge처럼 자간이 들어가 글자 위치가 소수로 떨어진다.
 */
private val TEXT_STYLES = listOf(
    TextStyle(fontSize = 16.sp, lineHeight = 24.sp, color = Color.Black),
    TextStyle(fontSize = 16.sp, lineHeight = 24.sp, color = Color.Black, letterSpacing = 0.5.sp),
)

/** 확인할 불릿 들여쓰기(g). 불릿 모양이 바뀌면 g가 바뀌므로, g를 바꾸는 것으로 불릿 교체를 대신한다. */
private val BULLET_INDENTS = listOf(24.dp, 48.dp)

/**
 * 알약이 보이는 모양일 뿐인지, 그리고 라벨이 가운데에 그려지는지 실제 layout과 픽셀로 확인한다.
 *
 * - 글자 스타일 x 불릿 들여쓰기 조합마다 본다.
 */
@RunWith(AndroidJUnit4::class)
class PillLayoutTest {

    @get:Rule
    val composeRule = createComposeRule()

    private class Config(val textStyle: TextStyle, val bulletIndent: Dp) {
        override fun toString() = "letterSpacing=${textStyle.letterSpacing}, g=$bulletIndent"
    }

    private val configs = TEXT_STYLES.flatMap { style -> BULLET_INDENTS.map { indent -> Config(style, indent) } }

    private val state = TextFieldState(STORED_TEXT)
    private var config by mutableStateOf(configs.first())
    private var getLayout: (() -> TextLayoutResult?)? = null
    private lateinit var textMeasurer: TextMeasurer

    private fun setContent() {
        composeRule.setContent {
            textMeasurer = rememberTextMeasurer()
            BulletListTextField(
                state = state,
                modifier = Modifier.fillMaxWidth().background(Color.White).testTag(FIELD_TAG),
                textStyle = config.textStyle,
                onTextLayout = { getLayout = it },
                bulletIndent = config.bulletIndent,
                pillStyle = PillStyle(color = PILL_COLOR),
            )
        }
    }

    /** 조합마다 [config]를 바꿔 다시 그린 뒤 [check]를 부른다. */
    private fun forEachConfig(check: (Config, TextLayoutResult) -> Unit) {
        setContent()
        for (current in configs) {
            config = current
            composeRule.waitForIdle()
            check(current, requireNotNull(getLayout?.invoke()))
        }
    }

    /**
     * 화면 layout의 글자 위치가, 같은 글을 알약 없이 그냥 들여써 그린 layout과 같다. 저장값도 그대로다.
     *
     * - 알약은 글자 색과 배경만 바꾼다. 그래서 알약이 생겨도 글자·커서가 움직이지 않고, 탭·선택이 일반 텍스트와 같다.
     * - 라벨·본문 글자 자리는 정확히 같아야 한다. 본문 시작(공백을 친 직후 커서 자리)도 여기 들어간다.
     * - 구분자 자리와 알약 문단 줄 끝은 [DELIMITER_TOLERANCE_PX]까지 허용한다.
     */
    @Test
    fun pillDoesNotChangeTextLayout() = forEachConfig { case, layout ->
        val indentSp = with(composeRule.density) { case.bulletIndent.toSp() }
        val plain = textMeasurer.measure(
            text = STORED_TEXT,
            style = case.textStyle.copy(textIndent = TextIndent(indentSp, indentSp)),
            constraints = Constraints(maxWidth = layout.size.width),
        )
        val looseOffsets = looseOffsets()
        for (offset in 0..STORED_TEXT.length) {
            val expected = plain.getHorizontalPosition(offset, usePrimaryDirection = true)
            val actual = layout.getHorizontalPosition(offset, usePrimaryDirection = true)
            val tolerance = if (offset in looseOffsets) DELIMITER_TOLERANCE_PX else POSITION_TOLERANCE_PX
            if (abs(expected - actual) > tolerance) {
                throw AssertionError("[$case] offset $offset x: expected=$expected, actual=$actual")
            }
            assertEquals("[$case] offset $offset 줄", plain.getLineForOffset(offset), layout.getLineForOffset(offset))
        }
        composeRule.runOnIdle { assertEquals("[$case] 저장값", STORED_TEXT, state.text.toString()) }
    }

    /** 알약 문단의 구분자 자리(`:` 앞, `:` 뒤)와 줄 끝 offset. [DELIMITER_TOLERANCE_PX]를 적용한다. */
    private fun looseOffsets(): Set<Int> = buildSet {
        forEachParagraph(STORED_TEXT) { start, end ->
            val pill = STORED_TEXT.pillRangeOf(start, end) ?: return@forEachParagraph
            add(pill.labelEnd)
            add(pill.backgroundEnd)
            add(end)
        }
    }

    /**
     * 캡처한 픽셀에서 알약 배경의 양 끝을 찾아, 라벨 앞뒤 여백이 같은지 본다.
     *
     * - 왼쪽 여백 = 라벨 시작 x - 알약 왼쪽 끝, 오른쪽 여백 = 알약 오른쪽 끝 - 라벨 끝 x.
     * - 줄 상자의 세로 가운데 한 줄을 훑는다. 둥근 모서리도 거기서는 끝까지 차 있다.
     */
    @Test
    fun drawnPillCentersLabel() = forEachConfig { case, layout ->
        val pixels = composeRule.onNodeWithTag(FIELD_TAG).captureToImage().toPixelMap()
        var paragraphStart = 0
        for (label in LABELS) {
            val pill = requireNotNull(STORED_TEXT.pillRangeOf(paragraphStart, STORED_TEXT.indexOf('\n', paragraphStart)))
            val line = layout.getLineForOffset(pill.labelStart)
            val y = ((layout.getLineTop(line) + layout.getLineBottom(line)) / 2).roundToInt()
            val pillXs = (0 until pixels.width).filter { pixels[it, y] == PILL_COLOR }
            val labelStartX = layout.getHorizontalPosition(pill.labelStart, usePrimaryDirection = true)
            val labelEndX = layout.getHorizontalPosition(pill.labelEnd, usePrimaryDirection = true)
            val leftPadding = labelStartX - pillXs.first()
            val rightPadding = pillXs.last() + 1 - labelEndX
            if (abs(leftPadding - rightPadding) > DRAWN_PADDING_TOLERANCE_PX) {
                throw AssertionError("[$case] '$label' 왼쪽 여백 $leftPadding / 오른쪽 여백 $rightPadding")
            }
            paragraphStart = STORED_TEXT.indexOf('\n', paragraphStart) + 1
        }
    }
}
