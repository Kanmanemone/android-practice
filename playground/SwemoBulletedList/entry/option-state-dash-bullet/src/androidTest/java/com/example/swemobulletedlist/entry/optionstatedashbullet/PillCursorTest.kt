package com.example.swemobulletedlist.entry.optionstatedashbullet

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInputSelection
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

private const val LABEL = "Label"
private const val BODY = "body text"

/** 맨 앞 sentinel([rememberBulletListTextFieldState]가 붙이는 문자)을 포함한 저장값. 알약 문단 하나다. */
private const val STORED_TEXT = "​$LABEL: $BODY"

/**
 * 알약도 일반 글자처럼 커서·선택이 되는지 확인한다.
 *
 * - 알약은 보이는 모양일 뿐이다. 라벨과 `: ` 사이 어디에 커서를 둬도, 드래그로 선택해도 메모장과 같아야 한다.
 * - 예전에는 알약 안에 온 커서를 바깥으로 밀어내는 규칙이 있어, 본문에서 알약 쪽으로 드래그하면 선택 끝이 순간이동했다.
 */
@RunWith(AndroidJUnit4::class)
class PillCursorTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val state = TextFieldState(STORED_TEXT)
    private var getLayout: (() -> TextLayoutResult?)? = null

    private val pill: PillRange = requireNotNull(STORED_TEXT.pillRangeOf(0, STORED_TEXT.length))

    private fun setContent() {
        composeRule.setContent {
            BulletListTextField(
                state = state,
                modifier = Modifier.fillMaxWidth().testTag(FIELD_TAG),
                textStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
                onTextLayout = { getLayout = it },
            )
        }
    }

    /**
     * 알약 안 모든 위치에 커서를 두거나, 본문 끝에서 알약 안까지 선택해도 그대로 남는다.
     *
     * - 드래그·방향키로 선택이 바뀔 때와 같은 길(InputTransformation)을 지난다. 여기서 선택이 밀리면 드래그 중에도 순간이동한다.
     * - 선택 방향은 비교하지 않는다. 테스트용 선택 액션(`SetSelection`)이 시작·끝을 정방향으로 바꿔 넣는다.
     */
    @Test
    fun selectionInsidePill_staysWhereItIsPut() {
        setContent()
        val field = composeRule.onNodeWithTag(FIELD_TAG)
        for (offset in pill.labelStart..pill.delimiterEnd) {
            field.performTextInputSelection(TextRange(offset))
            composeRule.runOnIdle { assertEquals("커서 $offset", TextRange(offset), state.selection) }

            val fromBodyEnd = TextRange(offset, STORED_TEXT.length)
            field.performTextInputSelection(fromBodyEnd)
            composeRule.runOnIdle {
                assertEquals("선택 $fromBodyEnd", fromBodyEnd, TextRange(state.selection.min, state.selection.max))
            }
        }
    }

    /**
     * 라벨 글자 사이를 탭하면 커서가 그 자리에 온다.
     *
     * - 실제 손가락 제스처로 확인한다. 커서가 라벨 앞이나 본문 시작으로 튀면 실패한다.
     * - 길게 눌러 끄는 선택은 Android 기본대로 단어 단위로 넓어져 라벨 앞에 붙는 게 정상이라, 순간이동과 구분되지 않는다. 그래서 탭으로 본다.
     */
    @Test
    fun tapInsideLabel_putsCursorThere() {
        setContent()
        composeRule.waitForIdle()
        val layout = requireNotNull(getLayout?.invoke())

        val targetOffset = pill.labelStart + LABEL.length / 2 // 라벨 가운데 글자 앞
        val line = layout.getLineForOffset(targetOffset)
        val target = Offset(
            x = layout.getHorizontalPosition(targetOffset, usePrimaryDirection = true),
            y = (layout.getLineTop(line) + layout.getLineBottom(line)) / 2,
        )

        composeRule.onNodeWithTag(FIELD_TAG).performTouchInput { click(target) }

        composeRule.runOnIdle { assertEquals(TextRange(targetOffset), state.selection) }
    }
}
