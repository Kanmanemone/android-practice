package com.example.swemobulletedlist.entry.optionstatepillbullet

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextIndent

/**
 * 텍스트 맨 앞에 항상 하나 깔아두는 보이지 않는 문자(zero-width space, U+200B).
 *
 * - Android는 지울 글자가 없는 위치에서 누른 Backspace를 편집으로 알려주지 않는다. 맨 앞에 sentinel을 깔아두면, 첫 줄 맨 앞 Backspace가 "sentinel 삭제"라는 편집으로 들어와 감지할 수 있다.
 * - 폭이 0인 문자라 화면에 그대로 둬도 보이지 않는다. 그래서 OutputTransformation으로 가릴 필요가 없다.
 */
private const val SENTINEL = '​'

/**
 * 화면용 텍스트에서 `\n` 대신 넣는 문자.
 *
 * - 문단 나눔은 [ParagraphStyle] 범위 경계가 맡는다. `\n`을 그대로 두면 문단마다 빈 줄이 하나씩 더 생긴다.
 * - 글자 수가 같아 화면 offset이 저장값과 똑같다.
 * - 공백이라 단어 경계가 된다. 더블탭 단어 선택이 다음 문단이나 `\n`까지 넘어가지 않는다.
 *     - 폭 0 문자(U+200B)는 단어 경계 규칙에서 앞 글자에 붙어, 단어 선택에 `\n`이 딸려 들어간다.
 * - 줄 끝 공백이라 줄 넘침에는 영향이 없다.
 */
private const val PARAGRAPH_BREAK = " "

/**
 * 불릿 리스트용 [TextFieldState]를 만든다.
 *
 * - 저장값 맨 앞에 [SENTINEL]을 붙여 둔다. [BulletListInputTransformation]은 편집이 일어날 때만 불리므로, 초기값은 여기서 맞춰야 한다.
 * - 이 상태에는 [BulletListInputTransformation]을 걸어야 한다.
 */
@Composable
internal fun rememberBulletListTextFieldState(initialText: String): TextFieldState =
    rememberTextFieldState(SENTINEL + initialText)

/**
 * 불릿 리스트의 [InputTransformation]. 아래 단계를 차례로 거친다.
 *
 * - 텍스트 편집뿐 아니라 탭·방향키 같은 선택 변경 때도 불린다.
 * - 각 단계는 앞 단계가 고친 버퍼를 이어받는다. 순서가 곧 우선순위다.
 * - 알약에는 커서 규칙을 두지 않는다. 알약은 보이는 모양일 뿐이고, 라벨과 `: `는 일반 글자처럼 커서·선택·드래그가 된다.
 */
internal val BulletListInputTransformation: InputTransformation by lazy { // 아래 단계들이 먼저 초기화되도록 lazy
    RemoveEmptyFirstLineOnBackspace
        .then(BlockBackspaceOnFirstLine)
        .then(RestoreMissingSentinel)
        .then(KeepCursorAfterSentinel)
}

/**
 * 불릿 리스트의 [OutputTransformation]을 만든다. 화면에 보일 텍스트만 고치고, 저장값은 건드리지 않는다.
 *
 * - 문단마다 [layoutOf]가 돌려준 들여쓰기로 [ParagraphStyle]을 건다.
 *     - 범위는 문단 시작부터 그 문단을 끝내는 `\n`까지다. 모든 문단에 빠짐없이 건다.
 *     - 문단을 끝내는 `\n`은 [PARAGRAPH_BREAK]로 바꾼다. 문단 나눔은 스타일 범위 경계가 맡는다.
 *     - `\n`을 그대로 두면, 스타일 범위가 갈리는 곳마다 Compose가 문단을 따로 떼어 레이아웃해 빈 줄이 하나 더 생긴다.
 * - 텍스트가 `\n`으로 끝나면, 화면용 텍스트 끝에 폭 0 문자([SENTINEL]과 같은 문자)를 하나 붙인다.
 *     - 그러지 않으면 마지막 빈 문단은 글자가 없어 스타일 범위가 비고, 들여쓰기가 걸리지 않는다.
 *     - 그러면 그 줄만 커서가 왼쪽 끝에 붙고, 불릿은 왼쪽 밖에 그려져 보이지 않는다.
 * - 알약 문단([pillRangeOf])은 구분자의 `:`에 [ParagraphLayout.pillColonStyle]을 건다. 공백은 그대로 둔다.
 *     - 글자를 빼지 않아 화면 offset이 저장값과 그대로 같다.
 * - 판단은 텍스트 내용으로만 한다. layout보다 먼저 돌기 때문이다.
 *
 * @param layoutOf 문단 범위(`start` ~ `end`, `end`는 `\n` 직전)를 받아 그 문단의 [ParagraphLayout]을 돌려준다.
 */
internal fun bulletListOutputTransformation(
    layoutOf: (text: CharSequence, start: Int, end: Int) -> ParagraphLayout,
) = OutputTransformation {
    if (length > 0 && charAt(length - 1) == '\n') {
        append(SENTINEL)
    }

    val text = toString() // 아래에서 `\n`을 바꾸기 전의 텍스트
    forEachParagraph(text) { _, end ->
        if (end < text.length) {
            replace(end, end + 1, PARAGRAPH_BREAK) // 글자 수가 같아 offset이 밀리지 않는다
        }
    }
    forEachParagraph(text) { start, end ->
        val styleEnd = if (end < text.length) end + 1 else end // 문단을 끝내는 `\n` 자리까지
        val layout = layoutOf(text, start, end)
        addStyle(ParagraphStyle(textIndent = layout.indent), start, styleEnd)
        val pill = text.pillRangeOf(start, end) ?: return@forEachParagraph
        layout.pillColonStyle?.let { addStyle(it, pill.labelEnd, pill.backgroundEnd) }
    }
}

/**
 * [bulletListOutputTransformation]이 문단 하나에 거는 스타일.
 *
 * @property indent 문단 들여쓰기.
 * @property pillColonStyle 알약 문단에서 구분자의 `:`에 거는 스타일. 투명하게 칠하고 폭을 맞춘다. 일반 문단은 `null`이다.
 */
internal class ParagraphLayout(val indent: TextIndent, val pillColonStyle: SpanStyle? = null)

/**
 * [text]를 `\n` 기준 문단으로 나눠 앞에서부터 [block]을 부른다.
 *
 * - `start`는 문단 첫 글자, `end`는 그 문단을 끝내는 `\n`의 위치다. 마지막 문단은 `end`가 텍스트 길이다.
 * - 빈 문단은 `start == end`다.
 * - Compose는 `\n` 문단 목록을 공개하지 않아 직접 나눈다.
 */
internal inline fun forEachParagraph(text: CharSequence, block: (start: Int, end: Int) -> Unit) {
    var start = 0
    while (true) {
        val newline = text.indexOf('\n', startIndex = start) // 없으면 -1
        val end = if (newline < 0) text.length else newline
        block(start, end)
        if (newline < 0) break
        start = newline + 1
    }
}

/**
 * 문단에서 글자가 시작하는 위치.
 *
 * - 첫 문단은 맨 앞 [SENTINEL]을 건너뛴다.
 */
internal fun CharSequence.paragraphContentStart(start: Int): Int =
    if (start == 0 && length > 0 && this[0] == SENTINEL) 1 else start
/* ───────────── Input 단계 ───────────── */

/**
 * 첫 줄이 비어 있을 때 그 줄 맨 앞에서 Backspace를 누르면, 첫 줄을 없앤다.
 *
 * - 블록 에디터의 "빈 항목 맨 앞 Backspace = 항목 삭제" 규칙이다.
 * - 지워진 sentinel을 되살리고, 빈 첫 줄 뒤의 `\n`을 지워 2번째 줄을 끌어올린다.
 */
private val RemoveEmptyFirstLineOnBackspace = InputTransformation {
    if (isOnlySentinelDeleted() && originalText.startsWith("$SENTINEL\n")) {
        revertAllChanges()
        delete(1, 2)
    }
}

/**
 * 첫 줄 맨 앞에서 누른 Backspace를 무시한다.
 *
 * - 첫 줄 앞에는 합칠 항목이 없으므로, sentinel만 지워진 편집은 통째로 되돌린다.
 */
private val BlockBackspaceOnFirstLine = InputTransformation {
    if (isOnlySentinelDeleted()) {
        revertAllChanges()
    }
}

/**
 * sentinel이 사라졌으면 맨 앞에 다시 넣는다.
 *
 * - 전체 선택 후 덮어쓰기처럼, Backspace가 아닌 이유로 sentinel이 지워진 경우를 처리한다.
 */
private val RestoreMissingSentinel = InputTransformation {
    if (!startsWithSentinel()) {
        insert(0, SENTINEL.toString())
    }
}

/** 커서/선택이 sentinel 앞(위치 0)에 오면 sentinel 바로 뒤로 옮긴다. */
private val KeepCursorAfterSentinel = InputTransformation {
    if (selection.min < 1) {
        selection = TextRange(selection.start.coerceAtLeast(1), selection.end.coerceAtLeast(1))
    }
}

/* ───────────── 헬퍼 ───────────── */

/** 버퍼가 [SENTINEL]로 시작하는지. */
private fun TextFieldBuffer.startsWithSentinel(): Boolean = length > 0 && charAt(0) == SENTINEL

/**
 * 이번 편집이 "맨 앞 sentinel 하나만 지운 것"인지. 즉 첫 줄 맨 앞에서 Backspace를 눌렀는지.
 *
 * - 편집 전에는 sentinel로 시작했고, 편집 후 텍스트가 편집 전에서 sentinel만 뺀 것과 같으면 참이다.
 */
private fun TextFieldBuffer.isOnlySentinelDeleted(): Boolean =
    originalText.startsWith(SENTINEL) &&
        asCharSequence().contentEquals(originalText.subSequence(1, originalText.length))
