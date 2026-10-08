package com.example.splitlabeltext.entry.optionstatedrawnbullet

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange

/**
 * 텍스트 맨 앞에 항상 하나 깔아두는 보이지 않는 문자(zero-width space, U+200B).
 *
 * - Android는 지울 글자가 없는 위치에서 누른 Backspace를 편집으로 알려주지 않는다. 맨 앞에 sentinel을 깔아두면, 첫 줄 맨 앞 Backspace가 "sentinel 삭제"라는 편집으로 들어와 감지할 수 있다.
 * - 폭이 0인 문자라 화면에 그대로 둬도 보이지 않는다. 그래서 OutputTransformation으로 가릴 필요가 없다.
 *     - OutputTransformation으로 화면 텍스트 길이를 바꾸면, IME 삭제(`deleteSurroundingText`) 처리 중 크래시가 난다 (foundation 1.10.4 버그).
 */
private const val SENTINEL = '​'

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

