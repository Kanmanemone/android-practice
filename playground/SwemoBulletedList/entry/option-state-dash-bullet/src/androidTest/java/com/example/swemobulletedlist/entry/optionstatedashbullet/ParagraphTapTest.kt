package com.example.swemobulletedlist.entry.optionstatedashbullet

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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val FIELD_TAG = "bulletListTextField"

/** 문단들. 일반 문단과 알약 문단을 섞는다. */
private val PARAGRAPHS = listOf("line1", "line2", "pill: body", "line4", "line5")

/** 맨 앞 sentinel([rememberBulletListTextFieldState]가 붙이는 문자)을 포함한 저장값. */
private val STORED_TEXT = "​" + PARAGRAPHS.joinToString("\n")

/**
 * 문단을 눌렀을 때 커서가 그 문단에 놓이는지 확인한다. 메모장과 같아야 한다.
 *
 * - 예전에는 문단 오른쪽 빈 곳을 누르면 커서가 다음 문단 맨 앞에 놓였다([ParagraphEndTapFix]).
 * - 문단마다 실제 탭 제스처로 확인한다.
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
        val layout = setContent()
        for ((index, range) in paragraphRanges().withIndex()) {
            val line = layout.getLineForOffset(range.start)
            tap(Offset(layout.size.width - 10f, layout.lineCenterY(line)))
            composeRule.runOnIdle { assertEquals("${index + 1}번째 문단 오른쪽", TextRange(range.end), state.selection) }
        }
    }

    /** 문단 왼쪽(불릿·알약 자리)을 누르면 커서가 그 문단 맨 앞에 온다. */
    @Test
    fun tapLeftOfParagraph_putsCursorAtItsStart() {
        val layout = setContent()
        for ((index, range) in paragraphRanges().withIndex()) {
            val line = layout.getLineForOffset(range.start)
            tap(Offset(1f, layout.lineCenterY(line)))
            composeRule.runOnIdle { assertEquals("${index + 1}번째 문단 왼쪽", TextRange(range.start), state.selection) }
        }
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

    private fun TextLayoutResult.lineCenterY(line: Int) = (getLineTop(line) + getLineBottom(line)) / 2
}
