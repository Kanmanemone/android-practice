package com.example.swemobulletedlist.entry.optionstatepillbullet

import android.annotation.SuppressLint
import android.content.ClipData
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.platform.AndroidClipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.nativeClipboardManager
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.text.TextRange

/**
 * 복사·잘라내기 때 화면용 텍스트 대신 [state]의 저장값을 클립보드에 넣는 [Clipboard].
 *
 * - `BasicTextField`는 OutputTransformation을 거친 화면용 텍스트에서 선택 부분을 복사한다.
 *     - 화면용 텍스트는 문단을 끝내는 `\n`이 공백으로 바뀌어 있다. 그대로 두면 복사한 글이 한 문단으로 합쳐진다.
 * - 이 필드 안쪽에만 `LocalClipboard`로 건다. 그 안에서 클립보드에 쓰는 쪽은 이 필드의 복사·잘라내기뿐이다.
 * - 클립보드에 들어온 글을 저장값의 선택 부분과 대조해, 화면용으로 바뀐 것이 맞을 때만 저장값으로 바꿔 넣는다.
 *     - 복사는 선택이 남아 있으므로 지금 선택 부분과 대조한다.
 *     - 잘라내기는 글자를 먼저 지우고 클립보드에 쓴다(Compose 1.12). 그래서 [editRecorder]가 남긴 "직전 편집 전" 선택 부분과 대조한다.
 *     - 둘 다 맞지 않으면 들어온 글을 그대로 쓴다.
 * - 읽기(붙여넣기)는 [base]에 그대로 넘긴다.
 * - [AndroidClipboard]를 구현해, 플랫폼 클립보드를 꺼내 쓰는 코드도 그대로 동작한다.
 */
@SuppressLint("VisibleForTests") // AndroidClipboard는 테스트용 표시가 붙어 있지만 공개 인터페이스다
internal class StoredTextClipboard(
    private val base: Clipboard,
    private val state: TextFieldState,
) : AndroidClipboard {

    /** 직전 편집 전의 저장값. [editRecorder]가 편집마다 덮어쓴다. */
    private var textBeforeLastEdit: CharSequence = ""

    /** 직전 편집 전의 선택. */
    private var selectionBeforeLastEdit: TextRange = TextRange.Zero

    /**
     * 편집마다 편집 전의 저장값과 선택을 남기는 [InputTransformation].
     *
     * - 이 필드의 `inputTransformation` 맨 끝에 붙인다. 글자는 고치지 않는다.
     */
    val editRecorder = InputTransformation {
        textBeforeLastEdit = originalText
        selectionBeforeLastEdit = originalSelection
    }

    override suspend fun getClipEntry(): ClipEntry? = base.getClipEntry()

    override suspend fun setClipEntry(clipEntry: ClipEntry?) {
        val visualText = clipEntry?.clipData?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.text
        val storedText = visualText?.let { storedTextFor(it) }
        if (clipEntry == null || storedText == null) {
            base.setClipEntry(clipEntry)
            return
        }
        val label = clipEntry.clipData.description.label
        base.setClipEntry(ClipData.newPlainText(label, storedText).toClipEntry())
    }

    override val clipboardManager: android.content.ClipboardManager
        get() = base.nativeClipboardManager

    /** 화면용 텍스트 [visualText]에 해당하는 저장값. 지금 선택(복사)과 직전 편집 전 선택(잘라내기)을 차례로 대조한다. */
    private fun storedTextFor(visualText: CharSequence): String? =
        sequenceOf(state.text to state.selection, textBeforeLastEdit to selectionBeforeLastEdit)
            .filterNot { (_, selection) -> selection.collapsed }
            .map { (text, selection) -> text.subSequence(selection.min, selection.max) }
            .firstOrNull { it.showsAs(visualText) }
            ?.let { stripSentinel(it) }

    /** 맨 앞 sentinel을 뺀다. 선택이 텍스트 맨 앞부터일 때만 들어 있다. */
    private fun stripSentinel(text: CharSequence): String =
        text.subSequence(text.paragraphContentStart(0), text.length).toString()
}

/**
 * 저장값 조각이 화면에 [visualText]로 보이는지.
 *
 * - 길이가 같고, 다른 글자는 저장값의 `\n`이 화면에서 공백으로 바뀐 자리뿐이어야 한다.
 */
private fun CharSequence.showsAs(visualText: CharSequence): Boolean =
    length == visualText.length && indices.all { this[it] == visualText[it] || (this[it] == '\n' && visualText[it] == ' ') }
