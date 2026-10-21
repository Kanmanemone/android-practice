package com.example.swemobulletedlist.entry.optionstatepillbullet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

/** 알약 라벨들. 글자 폭이 고른 것·좁은 것·넓은 것·한글·공백이 섞인 것을 고루 넣는다. */
private val LABELS = listOf("abc", "iii", "WWW", "가나다", "Label 1", "x")

/** 일반 문단. 일반 문단의 글자 시작 x가 곧 불릿 들여쓰기(g)다. */
private const val PLAIN_PARAGRAPH = "plain"

/** 맨 앞 sentinel([rememberBulletListTextFieldState]가 붙이는 문자)을 포함한 저장값. */
private val STORED_TEXT = "​" + (LABELS.map { "$it: body" } + PLAIN_PARAGRAPH).joinToString("\n")

/**
 * 가운데 정렬 허용 오차(px). 화면 픽셀 하나.
 *
 * - Android는 들여쓰기를 정수 px로 쓴다. 왼쪽 여백이 1px 단위로만 움직여, 좌우 차이를 1px 아래로 늘 맞출 수는 없다.
 */
private const val CENTER_TOLERANCE_PX = 1f

/** 본문 시작 허용 오차(px). 본문 시작은 정확히 맞추므로 부동소수 오차만 허용한다. */
private const val BODY_START_TOLERANCE_PX = 0.05f

/**
 * 확인할 글자 스타일.
 *
 * - 자간이 없는 스타일: 글자 위치가 정수 px로 떨어진다.
 * - 자간이 있는 스타일: Material3 bodyLarge처럼 자간이 들어가 글자 위치가 소수로 떨어진다.
 */
private val TEXT_STYLES = listOf(
    TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
)

/**
 * 알약 배치 규칙을 실제 layout으로 확인한다.
 *
 * - 불릿 들여쓰기(g)와 넘침 폭을 바꿔 가며 본다. 불릿 모양이 바뀌면 g가 바뀌므로, g를 바꾸는 것으로 불릿 교체를 대신한다.
 * - 조합마다 아래를 확인한다.
 *     - 가운데 정렬: 왼쪽 여백(넘침 폭 + 라벨 시작 x) = 오른쪽 여백(투명한 `:` 폭). 1px 안쪽.
 *     - 커서 고정: 본문 시작 x = 알약이 아닌 일반 문단으로 그렸을 때의 같은 자리. 정확히.
 *     - 저장값 보존: 화면용으로 바꾸고 그려도 저장값은 그대로다.
 * - 넘침 폭이 너무 커서 가운데 정렬이 안 되는 조합(들여쓰기가 0보다 작아지는 경우)은 넣지 않는다.
 */
@RunWith(AndroidJUnit4::class)
class PillLayoutTest {

    @get:Rule
    val composeRule = createComposeRule()

    private class Config(val textStyle: TextStyle, val bulletIndent: Dp, val overflow: Dp) {
        override fun toString() = "letterSpacing=${textStyle.letterSpacing}, g=$bulletIndent, overflow=$overflow"
    }

    private val configs = TEXT_STYLES.flatMap { style ->
        listOf(Dp.Unspecified, 24.dp, 48.dp).flatMap { indent ->
            listOf(0.dp, 8.dp).map { overflow -> Config(style, indent, overflow) }
        }
    }

    @Test
    fun pillIsCenteredAndBodyStartStaysPut() {
        val state = TextFieldState(STORED_TEXT)
        var config by mutableStateOf(configs.first())
        var getLayout: (() -> TextLayoutResult?)? = null
        lateinit var textMeasurer: TextMeasurer

        composeRule.setContent {
            textMeasurer = rememberTextMeasurer()
            BulletListTextField(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                textStyle = config.textStyle,
                onTextLayout = { getLayout = it },
                bulletIndent = config.bulletIndent,
                pillStyle = PillStyle(overflow = config.overflow),
            )
        }

        for (current in configs) {
            config = current
            composeRule.waitForIdle()
            composeRule.runOnIdle {
                val layout = requireNotNull(getLayout?.invoke())
                val overflowPx = with(composeRule.density) { current.overflow.toPx() }
                checkPills(layout, overflowPx, textMeasurer, current)
                assertEquals("저장값", STORED_TEXT, state.text.toString())
            }
        }
    }

    /** [layout]의 모든 알약 문단을 확인한다. */
    private fun checkPills(layout: TextLayoutResult, overflowPx: Float, textMeasurer: TextMeasurer, case: Config) {
        val text = layout.layoutInput.text
        val bulletIndentPx = layout.x(text.lastIndexOf(PLAIN_PARAGRAPH)) // 일반 문단의 글자 시작 x
        var pillCount = 0
        for (paragraph in text.paragraphStyles) {
            val end = if (paragraph.end < text.length) paragraph.end - 1 else paragraph.end
            val pill = text.pillRangeOf(paragraph.start, end) ?: continue
            pillCount++
            val label = text.substring(pill.labelStart, pill.labelEnd)

            val leftPadding = overflowPx + layout.x(pill.labelStart)
            val rightPadding = layout.x(pill.backgroundEnd) - layout.x(pill.labelEnd)
            assertNear("[$case] '$label' 왼쪽 여백 / 오른쪽 여백", leftPadding, rightPadding, CENTER_TOLERANCE_PX)

            val plainParagraph = text.substring(pill.labelStart, end) // 알약이 아니었다면 그대로 보였을 문단
            val expectedBodyStart = bulletIndentPx +
                textMeasurer.measure(plainParagraph, case.textStyle).x(pill.delimiterEnd - pill.labelStart)
            assertNear("[$case] '$label' 본문 시작", expectedBodyStart, layout.x(pill.delimiterEnd), BODY_START_TOLERANCE_PX)
        }
        assertEquals("[$case] 알약 수", LABELS.size, pillCount)
    }

    private fun TextLayoutResult.x(offset: Int) = getHorizontalPosition(offset, usePrimaryDirection = true)

    private fun assertNear(message: String, expected: Float, actual: Float, tolerance: Float) {
        if (abs(expected - actual) > tolerance) {
            throw AssertionError("$message: expected=$expected, actual=$actual")
        }
    }
}
