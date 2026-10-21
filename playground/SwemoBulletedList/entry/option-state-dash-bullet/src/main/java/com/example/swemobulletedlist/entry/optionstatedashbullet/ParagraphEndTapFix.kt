package com.example.swemobulletedlist.entry.optionstatedashbullet

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange

/**
 * 문단 오른쪽 빈 곳을 누르면 커서가 다음 문단 맨 앞으로 가는 문제를 바로잡는다.
 *
 * - 원인은 문단마다 레이아웃이 따로라는 데 있다.
 *     - 화면용 텍스트는 문단마다 `ParagraphStyle`을 걸어, Compose가 문단 하나를 Android 레이아웃 하나로 따로 만든다.
 *     - 문단을 끝내는 `\n`은 공백으로 바뀌어 그 문단의 마지막 글자가 된다([bulletListOutputTransformation]).
 *     - Android는 레이아웃 마지막 줄의 오른쪽 빈 곳을 누르면 줄 끝, 즉 그 공백 뒤를 돌려준다. 그 자리가 다음 문단 맨 앞이다.
 *     - 텍스트 전체가 레이아웃 하나인 필드에서는 마지막 줄이 아니면 줄 끝 글자 앞을 돌려줘서 이 문제가 없다.
 * - 누르는 동안의 손가락 위치를 기억해 두고([pointerModifier]), 그동안 바뀐 선택을 [inputTransformation]에서 고친다.
 *     - 선택 끝이 문단 맨 앞인데 손가락은 그 앞 문단의 마지막 줄에 있으면, 한 칸 앞(앞 문단 끝)으로 되돌린다.
 *     - 손가락을 뗀 뒤의 선택 변경(방향키 등)은 건드리지 않는다.
 * - 선택 손잡이는 별도 창(Popup)에서 끌려 손가락 위치가 여기로 오지 않는다. 손잡이를 줄 오른쪽 끝으로 끄는 경우는 고치지 못한다.
 *
 * @param getLayout 지금 화면의 텍스트 layout.
 * @param scrollOffset layout 좌표를 [pointerModifier]를 건 영역 좌표로 바꾸는 스크롤 이동량. 그리기 때와 같은 값이다.
 */
internal class ParagraphEndTapFix(
    private val getLayout: () -> TextLayoutResult?,
    private val scrollOffset: () -> Offset,
) {
    /** 누르고 있는 손가락의 위치(layout 좌표). 누르지 않으면 `null`이다. */
    private var pressedPosition: Offset? = null

    /**
     * 손가락 위치를 기억하는 [Modifier]. layout과 좌표가 같은 영역(텍스트 영역을 감싼 Box)에 건다.
     *
     * - 이벤트를 소비하지 않는다. 텍스트 필드의 탭·드래그 처리는 그대로 일어난다.
     * - `Initial` 단계에서 위치를 적는다. 텍스트 필드가 선택을 바꾸는 `Main` 단계보다 먼저다.
     * - `Final` 단계에서, 모든 손가락이 떨어졌으면 지운다. 같은 이벤트의 `Main` 단계에서 일어난 선택 변경까지는 위치가 남아 있다.
     */
    val pointerModifier: Modifier = Modifier.pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                event.changes.firstOrNull()?.let { pressedPosition = it.position - scrollOffset() }
                awaitPointerEvent(PointerEventPass.Final)
                if (event.changes.none { it.pressed }) pressedPosition = null
            }
        }
    }

    /**
     * 손가락으로 바뀐 선택의 양 끝을 고치는 [InputTransformation]. 글자는 고치지 않는다.
     *
     * - 글자를 고친 편집이나, 누르고 있지 않을 때의 선택 변경은 그대로 둔다.
     */
    val inputTransformation = InputTransformation {
        val position = pressedPosition ?: return@InputTransformation
        if (!asCharSequence().contentEquals(originalText)) return@InputTransformation
        val layout = getLayout() ?: return@InputTransformation
        val pressedLine = layout.getLineForVerticalPosition(position.y)
        val start = layout.pullBackToPressedLine(selection.start, pressedLine)
        val end = layout.pullBackToPressedLine(selection.end, pressedLine)
        if (start != selection.start || end != selection.end) {
            selection = TextRange(start, end)
        }
    }
}

/**
 * [offset]이 문단 맨 앞이고 그 앞 문단 끝이 [pressedLine]에 있으면, 앞 문단 끝(한 칸 앞)을 돌려준다. 아니면 그대로다.
 *
 * - 문단 맨 앞은 앞 글자가 문단 끝인 자리다. 화면용 텍스트에서 문단 끝은 문단 스타일 범위의 끝이다.
 */
private fun TextLayoutResult.pullBackToPressedLine(offset: Int, pressedLine: Int): Int {
    if (offset <= 0 || offset > layoutInput.text.length) return offset
    val isParagraphStart = layoutInput.text.paragraphStyles.any { it.end == offset && it.start < offset }
    if (!isParagraphStart) return offset
    val previousEndLine = getLineForOffset(offset - 1)
    return if (previousEndLine == pressedLine && getLineForOffset(offset) != pressedLine) offset - 1 else offset
}
