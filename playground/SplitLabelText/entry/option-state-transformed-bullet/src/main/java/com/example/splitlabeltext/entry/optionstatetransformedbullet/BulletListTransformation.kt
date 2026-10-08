package com.example.splitlabeltext.entry.optionstatetransformedbullet

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange

/** 각 줄 앞에 붙는 Bullet (글머리 기호). 뒤 간격까지 포함한다. */
private const val BULLET = "•  "

/**
 * 텍스트 맨 앞에 항상 하나 깔아두는 보이지 않는 문자(zero-width space, U+200B).
 *
 * - 불릿은 줄 앞의 `\n`을 `\n` + [BULLET]으로 교체해서 붙인다. 이 방식은 앞에 `\n`이 있는 2번째 줄부터만 통한다.
 * - 첫 줄은 `\n`으로 시작하지 않아 교체할 글자가 없다. 그래서 `\n` 대신 sentinel을 맨 앞에 두고, 이것을 [BULLET]으로 교체한다.
 * - 교체 구간의 끝은 정확히 매핑되므로, 첫 줄도 커서가 불릿 뒤에 선다.
 */
private const val SENTINEL = '​'

/**
 * 불릿 리스트용 [TextFieldState]를 만든다.
 *
 * - 저장값 맨 앞에 [SENTINEL]을 붙여 둔다. [BulletListInputTransformation]은 편집이 일어날 때만 불리므로, 초기값은 여기서 맞춰야 한다.
 * - 이 상태에는 [BulletListInputTransformation]과 [BulletListOutputTransformation]을 함께 걸어야 한다.
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
internal val BulletListInputTransformation: InputTransformation by lazy { // 이 함수보다, 이 함수의 내용에 있는 함수들이 먼저 초기화되도록 lazy
    RemoveEmptyFirstLineOnBackspace
        .then(BlockBackspaceOnFirstLine)
        .then(RestoreMissingSentinel)
        .then(KeepCursorAfterSentinel)
}

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

/* ───────────── Output 단계 ───────────── */

/**
 * 불릿 리스트의 [OutputTransformation]. 각 줄 맨 앞에 [BULLET]을 붙여 보여준다.
 *
 * - 화면에만 붙고 [TextFieldState]의 값에는 들어가지 않는다.
 * - 삽입(insert)이 아니라 교체(replace)로 붙인다. 삽입 지점의 커서는 불릿 앞에 그려지지만, 교체 구간의 끝은 교체된 글자 뒤로 정확히 매핑되기 때문이다.
 */
internal val BulletListOutputTransformation: OutputTransformation by lazy { // 이 함수보다, 이 함수의 내용에 있는 함수들이 먼저 초기화되도록 lazy
    BulletOnFirstLine
        .then(BulletOnOtherLines)
}

/**
 * 첫 줄: 맨 앞 [SENTINEL]을 [BULLET]으로 교체한다.
 *
 * - 첫 줄은 `\n`으로 시작하지 않아 [BulletOnOtherLines] 방식이 통하지 않으므로, sentinel을 교체 대상으로 쓴다.
 */
private val BulletOnFirstLine = OutputTransformation {
    if (startsWithSentinel()) {
        replace(0, 1, BULLET)
    }
}

/**
 * 2번째 줄부터: 줄 앞의 `\n`을 `\n` + [BULLET]으로 교체한다.
 *
 * - 끝에서 앞으로 훑는다. 앞에서 끝으로 훑으면, replace()로 길이가 늘어난 만큼 i가 어긋난다 (계산이 복잡해짐).
 */
private val BulletOnOtherLines = OutputTransformation {
    val original = toString()
    for (i in original.lastIndex downTo 0) {
        if (original[i] == '\n') {
            replace(i, i + 1, "\n$BULLET")
        }
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
 * 두 [OutputTransformation]을 차례로 실행하는 하나의 [OutputTransformation]을 만든다.
 *
 * - [InputTransformation.then]과 같은 모양으로 쓰려고 만든 것이다. OutputTransformation에는 기본 제공 then이 없다.
 */
private fun OutputTransformation.then(next: OutputTransformation): OutputTransformation {
    val first = this
    return OutputTransformation {
        with(first) { transformOutput() }
        with(next) { transformOutput() }
    }
}
