package com.example.swemobulletedlist.entry.optionstatepillbullet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val FIELD_TAG = "bulletListTextField"

/** 문단들. 일반 문단, 빈 문단, 알약 문단을 섞는다. */
private val PARAGRAPHS = listOf("line1", "", "pill: body", "line4", "line5")

/** 맨 앞 sentinel([rememberBulletListTextFieldState]가 붙이는 문자)을 포함한 저장값. */
private val STORED_TEXT = "​" + PARAGRAPHS.joinToString("\n")

/** 화면 위 위치를 훑을 때의 가로 간격(px). */
private const val SCAN_STEP_PX = 5f

/**
 * 문단 위 어디를 누르거나 끌어도 커서·선택 끝이 그 문단 안에 놓이는지 확인한다. 메모장과 같아야 한다.
 *
 * - 예전에는 문단 오른쪽 빈 곳을 누르거나 선택 손잡이를 거기로 끌면 다음 문단 맨 앞에 놓였다.
 *     - 문단마다 `ParagraphStyle`을 걸어 레이아웃이 문단마다 따로였고, `\n`을 공백으로 바꿔 그 공백 뒤가 후보에 들어갔기 때문이다.
 *     - 지금은 일반 텍스트 필드처럼 `\n`을 그대로 둔 레이아웃 하나라 이런 일이 없다.
 * - 탭, 길게 눌러 끌기, 선택 손잡이는 모두 `TextLayoutResult.getOffsetForPosition`으로 위치를 offset으로 바꾼다.
 *     - 손잡이는 별도 창(Popup)이라 테스트에서 직접 끌기 어렵다. 같은 계산을 쓰는 다른 두 길과 그 계산 자체로 확인한다.
 */
@RunWith(AndroidJUnit4::class)
class ParagraphTapTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val state = TextFieldState(STORED_TEXT)
    private var getLayout: (() -> TextLayoutResult?)? = null

    private fun setContent(): TextLayoutResult {
        composeRule.setContent {
            BulletListTextField(
                state = state,
                modifier = Modifier.fillMaxWidth().testTag(FIELD_TAG),
                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
                onTextLayout = { getLayout = it },
            )
        }
        composeRule.waitForIdle()
        return requireNotNull(getLayout?.invoke())
    }

    /** 문단 오른쪽 빈 곳(필드 오른쪽 끝 근처)을 누르면 커서가 그 문단 끝에 온다. */
    @Test
    fun tapRightOfParagraph_putsCursorAtItsEnd() {
        val layout = setContent(); android.util.Log.i("EXP", (0..3).map { "L$it top=${layout.getLineTop(it)} bottom=${layout.getLineBottom(it)} hit=${layout.getOffsetForPosition(androidx.compose.ui.geometry.Offset(5f, (layout.getLineTop(it)+layout.getLineBottom(it))/2))}" }.toString() + " h=${layout.size.height} lines=${layout.lineCount}")
        for ((index, range) in paragraphRanges().withIndex()) {
            tap(Offset(layout.size.width - 10f, layout.centerYOf(range)))
            composeRule.runOnIdle { assertEquals("${index + 1}번째 문단 오른쪽", TextRange(range.end), state.selection) }
        }
    }

    /** 문단 왼쪽(불릿·알약 자리)을 누르면 커서가 그 문단 맨 앞에 온다. */
    @Test
    fun tapLeftOfParagraph_putsCursorAtItsStart() {
        val layout = setContent()
        for ((index, range) in paragraphRanges().withIndex()) {
            tap(Offset(1f, layout.centerYOf(range)))
            composeRule.runOnIdle { assertEquals("${index + 1}번째 문단 왼쪽", TextRange(range.start), state.selection) }
        }
    }

    /**
     * 문단 줄 위 어느 x든 위치를 offset으로 바꾸면 그 문단 안(문단 시작 ~ 문단 끝)이다.
     *
     * - 탭·끌기·선택 손잡이가 모두 쓰는 계산이다. 손잡이를 줄 오른쪽 끝으로 끌 때도 이 값이 쓰인다.
     * - 필드 왼쪽 끝부터 오른쪽 끝까지 [SCAN_STEP_PX] 간격으로 훑는다.
     * - 첫 문단 왼쪽 끝은 맨 앞 sentinel 앞(0)이 나올 수 있다. 실제 커서는 sentinel 규칙이 sentinel 뒤로 옮긴다.
     */
    @Test
    fun offsetForPosition_anywhereOnParagraphLine_staysInParagraph() {
        val layout = setContent()
        for ((index, range) in paragraphRanges().withIndex()) {
            val y = layout.centerYOf(range)
            val allowed = (if (index == 0) 0 else range.start)..range.end
            var x = 0f
            while (x <= layout.size.width) {
                val offset = layout.getOffsetForPosition(Offset(x, y))
                assertTrue("${index + 1}번째 문단 x=$x → $offset (범위 $allowed)", offset in allowed)
                x += SCAN_STEP_PX
            }
        }
    }

    /**
     * 첫 문단 단어를 길게 누른 뒤 4번째 문단 오른쪽 빈 곳까지 끌면, 선택이 4번째 문단 끝에서 멈춘다.
     *
     * - 선택 손잡이를 끌 때와 같은 계산으로 선택 끝을 정한다. 다음 문단 맨 앞까지 넘어가면 실패한다.
     */
    @Test
    fun longPressDragToRightOfParagraph_selectsUpToItsEnd() {
        val layout = setContent()
        val ranges = paragraphRanges()
        val first = ranges[0]
        val target = ranges[3]
        val start = Offset(layout.getBoundingBox(first.start + 1).center.x, layout.centerYOf(first))
        val end = Offset(layout.size.width - 10f, layout.centerYOf(target))

        composeRule.onNodeWithTag(FIELD_TAG).performTouchInput {
            down(start)
            advanceEventTime(viewConfiguration.longPressTimeoutMillis + 100)
            moveTo(Offset(end.x, start.y)) // 첫 문단 오른쪽 끝을 지나
            moveTo(end) // 4번째 문단 오른쪽 끝까지
            up()
        }

        composeRule.runOnIdle { assertEquals(TextRange(first.start, target.end), state.selection) }
    }

    private fun tap(position: Offset) {
        composeRule.onNodeWithTag(FIELD_TAG).performTouchInput { click(position) }
    }

    /** 저장값에서 문단마다 글자 범위. 첫 문단은 sentinel 뒤부터다. */
    private fun paragraphRanges(): List<TextRange> {
        var start = 1
        return PARAGRAPHS.map { paragraph ->
            TextRange(start, start + paragraph.length).also { start += paragraph.length + 1 }
        }
    }

    /** [range] 문단 첫 줄의 세로 가운데. */
    private fun TextLayoutResult.centerYOf(range: TextRange): Float {
        val line = getLineForOffset(range.start)
        return (getLineTop(line) + getLineBottom(line)) / 2
    }
}
