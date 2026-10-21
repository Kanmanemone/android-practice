package com.example.swemobulletedlist.entry.optionstatedashbullet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val FIELD_TAG = "bulletListTextField"

/** 알약 문단과 일반 문단이 섞인 붙여넣기 텍스트. */
private const val PASTED_TEXT = "xxx: aaaa\ndddddd\nffff: ssss"

/**
 * 붙여넣기 → 전체 잘라내기 → 다시 붙여넣기를 해도 화면이 그대로인지 확인한다.
 *
 * - 실제 화면([OptionStateDashBulletScreen])을 띄워, 테마 글꼴·알약 설정까지 같은 조건에서 본다.
 * - 화면은 필드 영역을 캡처해 픽셀 단위로 비교한다.
 *     - 캡처 전에 포커스를 뺀다. 커서 깜빡임과 선택 손잡이가 비교에 끼지 않는다.
 * - 텍스트는 저장값으로도 비교한다.
 */
@RunWith(AndroidJUnit4::class)
class PasteCutRoundTripTest {

    @get:Rule
    val composeRule = createComposeRule()

    /**
     * 캡처 전에 포커스를 넘겨받는 빈 칸.
     *
     * - 화면에 포커스를 받을 곳이 필드 하나뿐이면 `clearFocus()`를 불러도 필드가 다시 포커스를 받는다.
     */
    private val focusSink = FocusRequester()

    private val field: SemanticsNodeInteraction
        get() = composeRule.onNodeWithTag(FIELD_TAG)

    @Test
    fun pasteCutPaste_showsSameScreen() {
        composeRule.setContent {
            Box {
                OptionStateDashBulletScreen(modifier = Modifier.testTag(FIELD_TAG), initialText = "")
                Box(Modifier.size(1.dp).focusRequester(focusSink).focusable()) // 필드 바깥(바깥 padding 자리)에 둔다
            }
        }

        setClipboard(PASTED_TEXT)
        paste()
        val firstText = fieldText()
        val firstScreen = captureUnfocused()
        assertTrue("붙여넣은 텍스트가 필드에 없다: $firstText", firstText.endsWith(PASTED_TEXT))

        cutAll()
        assertEquals("잘라낸 글", PASTED_TEXT, clipboardText())
        paste()
        val secondText = fieldText()
        val secondScreen = captureUnfocused()

        assertEquals(firstText, secondText)
        assertSameBitmap(firstScreen, secondScreen)
    }

    /** 시스템 클립보드에 [text]를 넣는다. 필드의 붙여넣기는 시스템 클립보드에서 읽는다. */
    private fun setClipboard(text: String) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            systemClipboard().setPrimaryClip(ClipData.newPlainText("test", text))
        }
    }

    /**
     * 시스템 클립보드의 글.
     *
     * - 잘라낸 글이 그대로 들어갔는지 본다. 화면용 텍스트가 들어가면 문단 사이 `\n`이 공백으로 바뀌어 있다.
     */
    private fun clipboardText(): String {
        var text = ""
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            text = systemClipboard().primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
        }
        return text
    }

    private fun systemClipboard(): ClipboardManager =
        InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    /** 필드에 포커스를 주고 커서를 맨 끝에 둔 뒤 붙여넣는다. */
    private fun paste() {
        field.performClick()
        field.performTextInputSelection(TextRange(fieldText().length))
        field.performSemanticsAction(SemanticsActions.PasteText)
        composeRule.waitForIdle()
    }

    /**
     * 필드 내용을 모두 선택해 잘라낸다.
     *
     * - 선택 시작을 0으로 줘도 sentinel 규칙이 sentinel 뒤로 옮긴다. 사용자가 전체 선택한 것과 같다.
     * - 잘라낸 글자는 클립보드로 가서, 다음 [paste]가 그대로 다시 붙인다.
     */
    private fun cutAll() {
        field.performClick()
        field.performTextInputSelection(TextRange(0, fieldText().length))
        field.performSemanticsAction(SemanticsActions.CutText)
        composeRule.waitForIdle()
    }

    /**
     * 필드의 저장값.
     *
     * - `InputText`를 읽는다. `EditableText`는 OutputTransformation을 거친 화면용 텍스트라 문단 사이 `\n`이 공백으로 바뀌어 있다.
     * - 맨 앞 sentinel까지 들어 있다.
     */
    private fun fieldText(): String =
        field.fetchSemanticsNode().config.getOrNull(SemanticsProperties.InputText)?.text.orEmpty()

    /**
     * 포커스를 뺀 뒤 필드 영역을 캡처한다.
     *
     * - 포커스가 남아 있으면 커서가 깜빡여, 같은 화면이라도 캡처 시점에 따라 달라진다.
     */
    private fun captureUnfocused(): Bitmap {
        composeRule.runOnIdle { focusSink.requestFocus() }
        field.assertIsNotFocused()
        return field.captureToImage().asAndroidBitmap()
    }

    /** 두 캡처가 크기와 모든 픽셀까지 같은지 확인한다. 다르면 다른 픽셀 수를 알려 준다. */
    private fun assertSameBitmap(expected: Bitmap, actual: Bitmap) {
        assertEquals("캡처 크기", expected.width to expected.height, actual.width to actual.height)
        val expectedPixels = IntArray(expected.width * expected.height)
        val actualPixels = IntArray(actual.width * actual.height)
        expected.getPixels(expectedPixels, 0, expected.width, 0, 0, expected.width, expected.height)
        actual.getPixels(actualPixels, 0, actual.width, 0, 0, actual.width, actual.height)
        val diffCount = expectedPixels.indices.count { expectedPixels[it] != actualPixels[it] }
        assertEquals("다른 픽셀 수", 0, diffCount)
    }
}
