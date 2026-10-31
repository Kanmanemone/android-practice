package com.example.swemobulletedlist.entry.testbed

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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val FIELD_TAG = "bulletedListTextField"

/** Pill 문단, 빈 문단, 일반 문단을 섞은 글. */
private const val MIXED_TEXT = "xxx: aaaa\n\n> dddddd\nffff: ssss\nplain"

/**
 * 복사·잘라내기를 해도 문단이 유지되는지 사용자 시점에서 확인한다.
 *
 * - 실제 화면([TestbedBulletedListScreen])을 띄워 필드를 조작하고, 결과는 시스템 클립보드와 화면에서 읽는다.
 */
@RunWith(AndroidJUnit4::class)
class BulletedListCopyTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val field get() = composeTestRule.onNodeWithTag(FIELD_TAG)

    private val systemClipboard
        get() = ApplicationProvider.getApplicationContext<Context>().getSystemService(ClipboardManager::class.java)

    /**
     * 캡처 전에 포커스를 넘겨받는 빈 칸.
     *
     * - 화면에 포커스를 받을 곳이 필드 하나뿐이면 `clearFocus()`를 불러도 필드가 다시 포커스를 받는다.
     */
    private val focusSink = FocusRequester()

    @Test
    fun copy_keepsParagraphBreaks() {
        showField("우유\n계란\n빵")

        select("우유\n계란")
        copy()

        assertEquals("우유\n계란", clipboardText())
    }

    @Test
    fun cut_keepsParagraphBreaks() {
        showField("우유\n계란\n빵")

        select("계란\n빵")
        cut()

        assertEquals("계란\n빵", clipboardText())
    }

    /** Ctrl+C는 메뉴 복사와 달리 선택을 유지한 채 클립보드에 쓴다. */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun copyWithKeyboard_keepsParagraphBreaks() {
        showField("우유\n계란\n빵")

        select("우유\n계란")
        field.performKeyInput { withKeyDown(Key.CtrlLeft) { pressKey(Key.C) } }
        composeTestRule.waitForIdle()

        assertEquals("우유\n계란", clipboardText())
    }

    @Test
    fun copy_keepsSpacesInsideParagraph() {
        showField("우유 사기\n계란 사기")

        select("우유 사기\n계란 사기")
        copy()

        assertEquals("우유 사기\n계란 사기", clipboardText())
    }

    @Test
    fun copy_textEndingWithNewline_keepsLastBreak() {
        showField("우유\n계란\n")

        select("우유\n계란\n")
        copy()

        assertEquals("우유\n계란\n", clipboardText())
    }

    /** 선택이 `\n`에서 끝나면 그 `\n`까지 들어간다. */
    @Test
    fun copy_upToParagraphBreak_keepsThatBreakOnly() {
        showField("우유\n계란\n빵")

        select("우유\n")
        copy()

        assertEquals("우유\n", clipboardText())
    }

    @Test
    fun cutThenPaste_restoresTextAndScreen() {
        showField(MIXED_TEXT)
        val textBefore = fieldText()
        val screenBefore = captureUnfocused()

        select(MIXED_TEXT)
        cut()
        paste()

        assertEquals("필드의 글", textBefore, fieldText())
        assertTrue("필드의 화면", screenBefore.sameAs(captureUnfocused()))
    }

    private fun showField(text: String) {
        composeTestRule.setContent {
            Box {
                TestbedBulletedListScreen(modifier = Modifier.testTag(FIELD_TAG), initialText = text)
                Box(Modifier.size(1.dp).focusRequester(focusSink).focusable()) // focusSink 자리
            }
        }
        composeTestRule.runOnIdle {
            systemClipboard.setPrimaryClip(ClipData.newPlainText("", "")) // 이전 테스트가 남긴 글로 통과하지 않게 비운다
        }
    }

    /** 필드에서 [part] 부분을 선택한다. */
    private fun select(part: String) {
        val start = fieldText().indexOf(part)
        field.performClick()
        field.performTextInputSelection(TextRange(start, start + part.length))
    }

    private fun copy() {
        field.performSemanticsAction(SemanticsActions.CopyText)
        composeTestRule.waitForIdle()
    }

    private fun cut() {
        field.performSemanticsAction(SemanticsActions.CutText)
        composeTestRule.waitForIdle()
    }

    private fun paste() {
        field.performClick()
        field.performSemanticsAction(SemanticsActions.PasteText)
        composeTestRule.waitForIdle()
    }

    private fun clipboardText(): String = composeTestRule.runOnIdle {
        systemClipboard.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
    }

    /** 필드의 글. */
    private fun fieldText(): String =
        field.fetchSemanticsNode().config.getOrNull(SemanticsProperties.InputText)?.text.orEmpty()

    /** 포커스를 뺀 뒤 필드 영역을 캡처한다. 포커스가 남아 있으면 커서가 깜빡여 캡처 시점마다 달라진다. */
    private fun captureUnfocused(): Bitmap {
        composeTestRule.runOnIdle { focusSink.requestFocus() }
        field.assertIsNotFocused()
        return field.captureToImage().asAndroidBitmap()
    }
}
