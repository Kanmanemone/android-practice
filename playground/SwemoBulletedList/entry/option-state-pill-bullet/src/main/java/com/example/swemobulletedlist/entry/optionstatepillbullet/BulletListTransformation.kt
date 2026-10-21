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
 */
internal val BulletListInputTransformation: InputTransformation by lazy { // 아래 단계들이 먼저 초기화되도록 lazy
    RemoveEmptyFirstLineOnBackspace
        .then(BlockBackspaceOnFirstLine)
        .then(RestoreMissingSentinel)
        .then(KeepCursorAfterSentinel)
        .then(KeepCursorOutOfPill)
}

/**
 * 불릿 리스트의 [OutputTransformation]을 만든다. 화면에 보일 텍스트만 고치고, 저장값은 건드리지 않는다.
 *
 * - 문단마다 [indentOf]가 돌려준 들여쓰기로 [ParagraphStyle]을 건다.
 *     - 범위는 문단 시작부터 그 문단을 끝내는 `\n`까지다. 모든 문단에 빠짐없이 건다.
 *     - 문단을 끝내는 `\n`은 [PARAGRAPH_BREAK]로 바꾼다. 문단 나눔은 스타일 범위 경계가 맡는다.
 *     - `\n`을 그대로 두면, 스타일 범위가 갈리는 곳마다 Compose가 문단을 따로 떼어 레이아웃해 빈 줄이 하나 더 생긴다.
 * - 텍스트가 `\n`으로 끝나면, 화면용 텍스트 끝에 폭 0 문자([SENTINEL]과 같은 문자)를 하나 붙인다.
 *     - 그러지 않으면 마지막 빈 문단은 글자가 없어 스타일 범위가 비고, 들여쓰기가 걸리지 않는다.
 *     - 그러면 그 줄만 커서가 왼쪽 끝에 붙고, 불릿은 왼쪽 밖에 그려져 보이지 않는다.
 * - 알약 문단([pillRangeOf])은 구분자의 `:`에 [pillColonStyle]을 건다. 공백은 그대로 둔다.
 *     - 글자를 빼지 않아 화면 offset이 저장값과 그대로 같다.
 * - 판단은 텍스트 내용으로만 한다. layout보다 먼저 돌기 때문이다.
 *
 * @param pillColonStyle 알약 문단의 구분자 `:`에 거는 스타일. 투명하게 칠하고 폭을 맞춘다.
 * @param indentOf 문단 범위(`start` ~ `end`, `end`는 `\n` 직전)를 받아 그 문단의 들여쓰기를 돌려준다.
 */
internal fun bulletListOutputTransformation(
    pillColonStyle: SpanStyle,
    indentOf: (text: CharSequence, start: Int, end: Int) -> TextIndent,
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
        addStyle(ParagraphStyle(textIndent = indentOf(text, start, end)), start, styleEnd)
        text.pillRangeOf(start, end)?.let { pill ->
            addStyle(pillColonStyle, pill.labelEnd, pill.backgroundEnd)
        }
    }
}

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

/**
 * 커서/선택의 양 끝이 알약 안에 오면 알약 바깥으로 옮긴다. 알약은 한 덩어리처럼 움직인다.
 *
 * - 알약 안은 라벨 첫 글자 뒤부터 구분자 공백 앞까지다. 라벨 첫 글자 앞(문단 시작)과 공백 뒤(본문 시작)는 바깥이다.
 * - 기본은 본문 시작으로 민다. 알약을 탭하면 알약 뒤에 커서가 온다.
 *     - 그 자리에서 Backspace를 누르면 공백이 지워져 알약이 풀리고 `라벨:`이 보인다. `: ` 입력으로 만들고 공백 삭제로 푸는 대칭이다.
 * - 본문 시작에서 왼쪽으로 한 칸 오면 라벨 앞으로 보낸다. 알약을 건너뛰고, 알약 뒤로 되돌아가 갇히지 않는다.
 * - 글자를 고친 편집은 건드리지 않는다. 라벨 앞에서 글자를 치면 라벨을 이어서 고칠 수 있다.
 * - sentinel 규칙 뒤에 둔다. 첫 문단의 알약은 sentinel 바로 뒤에서 시작한다.
 */
private val KeepCursorOutOfPill = InputTransformation {
    val text = asCharSequence()
    if (!text.contentEquals(originalText)) return@InputTransformation // 글자를 고친 편집
    val start = text.offsetOutOfPill(selection.start, originalSelection.start)
    val end = text.offsetOutOfPill(selection.end, originalSelection.end)
    if (start != selection.start || end != selection.end) {
        selection = TextRange(start, end)
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

/**
 * [offset]이 알약 안이면 알약 바깥 위치를, 아니면 그대로 돌려준다. 규칙은 [KeepCursorOutOfPill]을 따른다.
 *
 * @param originalOffset 이번 편집 전의 위치. 왼쪽으로 움직였는지 판단한다.
 */
private fun CharSequence.offsetOutOfPill(offset: Int, originalOffset: Int): Int {
    val paragraphStart = lastIndexOf('\n', startIndex = offset - 1) + 1 // 앞에 `\n`이 없으면 0
    val paragraphEnd = indexOf('\n', startIndex = offset).let { if (it < 0) length else it }
    val pill = pillRangeOf(paragraphStart, paragraphEnd) ?: return offset
    if (offset <= pill.labelStart || offset >= pill.delimiterEnd) return offset

    return if (originalOffset == pill.delimiterEnd) pill.labelStart else pill.delimiterEnd
}
