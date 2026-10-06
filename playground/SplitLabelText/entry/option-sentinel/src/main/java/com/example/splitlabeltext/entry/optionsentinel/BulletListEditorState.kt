package com.example.splitlabeltext.entry.optionsentinel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.util.concurrent.atomic.AtomicLong

/**
 * 불릿 리스트 에디터의 상태(항목 목록)와 편집 연산을 소유하는 상태 홀더.
 *
 * - UI(Composable) == 사용자는 [itemStates]를 그리고 입력 변화를 [onTextChange]로만 넘긴다.
 * - 불릿 아이템 분할(Enter, 여러 줄 붙여넣기) 또는 병합(맨 앞 Backspace)은 전부 [onTextChange] 안에서 판단한다.
 * - 포커스 이동은 여기서 직접 하지 않고 [pendingFocusId]에 "다음에 포커스 줄 항목 id" 만 세팅한다 (실제로 포커스를 주는 건 UI가 담당).
 */
@Stable
internal class BulletListEditorState(initialTexts: List<String>) {

    private val generatedId = AtomicLong(0L)

    private val _itemStates = mutableStateListOf<BulletItemState>().apply {
        initialTexts.ifEmpty { listOf("") }
            .forEach { add(BulletItemState(generatedId.getAndIncrement(), it)) }
    }

    /** 현재 항목 상태 목록. 읽기 전용 뷰. */
    val itemStates: List<BulletItemState> get() = _itemStates

    /**
     * 다음에 포커스를 줄 항목의 id.
     *
     * - 분할/병합이 세팅하고, UI 가 해당 항목에 포커스를 준 뒤 [clearPendingFocus]로 되돌린다.
     * - `null`이면 대상 없음.
     * - 사용자가 엔터 키 연타 시 최신 id 로 덮여 마지막 항목만 남는다.
     */
    var pendingFocusId: Long? by mutableStateOf(null)
        private set

    /** UI 가 [pendingFocusId] 항목에 포커스를 준 뒤 호출해 신호를 내린다. */
    fun clearPendingFocus() {
        pendingFocusId = null
    }

    /**
     * [androidx.compose.foundation.text.BasicTextField]의 onValueChange 진입점.
     *
     * 세 갈래로 분기한다.
     * 1. 맨 앞 sentinel 만 정확히 하나 사라졌다면 "보이는 위치 0에서 Backspace" → [mergeIntoPrevious].
     * 2. 보이는 텍스트에 개행이 있다면 (타이핑 Enter든 여러 줄 붙여넣기든) → [splitByNewlines].
     * 3. 그 밖에는 sentinel 을 첫 글자로 유지하고, 커서가 sentinel 앞(위치 0)으로 넘어가지 못하게 막는다.
     */
    fun onTextChange(itemState: BulletItemState, proposed: TextFieldValue) {
        val old = itemState.value

        val sentinelRemoved = old.text.startsWith(SENTINEL) &&
            !proposed.text.startsWith(SENTINEL) &&
            proposed.text == old.text.substring(SENTINEL.length)
        if (sentinelRemoved) {
            mergeIntoPrevious(itemState)
            return
        }

        val normalized = withSentinelPrefix(proposed)

        if ('\n' in normalized.text) {
            splitByNewlines(itemState, normalized)
            return
        }

        itemState.value = normalized
    }

    /**
     * [normalized]의 보이는 텍스트를 개행마다 나눈다.
     *
     * - 첫 조각은 [itemState]에 남기고, 나머지는 새 항목으로 삽입한다.
     * - 타이핑 Enter(조각 2개, 뒤 조각이 빈 문자열)와 여러 줄 붙여넣기(조각 N개)를 한 경로로 처리한다.
     * - 커서가 있던 조각으로 포커스를 옮기고, 그 조각 안에서의 상대 위치에 커서를 둔다.
     */
    private fun splitByNewlines(itemState: BulletItemState, normalized: TextFieldValue) {
        val index = _itemStates.indexOf(itemState)
        if (index < 0) return

        val visible = normalized.text.substring(SENTINEL.length)
        val parts = visible.split('\n')
        val caretInVisible = (normalized.selection.max - SENTINEL.length)
            .coerceIn(0, visible.length)

        /** 커서가 어느 조각(caretPart)의 어느 위치(caretOffset)에 있는지 찾는다. */
        var partStart = 0
        var caretPart = parts.lastIndex
        var caretOffset = parts.last().length
        for ((i, part) in parts.withIndex()) {
            if (caretInVisible <= partStart + part.length) {
                caretPart = i
                caretOffset = caretInVisible - partStart
                break
            }
            partStart += part.length + 1 // '\n' 한 글자
        }

        val head = parts.first()
        itemState.value = TextFieldValue(
            text = SENTINEL + head,
            selection = TextRange(SENTINEL.length + if (caretPart == 0) caretOffset else head.length),
        )

        val inserted = ArrayList<BulletItemState>(parts.size - 1)
        var at = index + 1
        for (part in parts.drop(1)) {
            val newItemState = BulletItemState(generatedId.getAndIncrement(), part)
            _itemStates.add(at++, newItemState)
            inserted += newItemState
        }

        val focusItemState = if (caretPart == 0) itemState else inserted[caretPart - 1]
        if (focusItemState !== itemState) {
            focusItemState.value = focusItemState.value.copy(
                selection = TextRange(SENTINEL.length + caretOffset),
            )
        }
        pendingFocusId = focusItemState.id
    }

    /**
     * 보이는 위치 0에서 Backspace: [itemState]을 없애고 그 내용을 이전 항목 끝에 이어 붙인다.
     *
     * - 이전 항목이 없으면 sentinel 만 복구하고 아무것도 하지 않는다.
     */
    private fun mergeIntoPrevious(itemState: BulletItemState) {
        val index = _itemStates.indexOf(itemState)
        val tail = itemState.visibleText

        if (index <= 0) {
            itemState.value = TextFieldValue(SENTINEL + tail, selection = TextRange(SENTINEL.length))
            return
        }

        val prev = _itemStates[index - 1]
        val joinAt = prev.value.text.length
        prev.value = TextFieldValue(
            text = prev.value.text + tail,
            selection = TextRange(joinAt), // 병합 지점에 커서
        )
        pendingFocusId = prev.id
        _itemStates.removeAt(index)
    }

    /** [value]의 text 가 sentinel 로 시작하도록 보정하고, 커서/선택이 sentinel 앞에 있으면 sentinel 바로 뒤로 옮긴다. */
    private fun withSentinelPrefix(value: TextFieldValue): TextFieldValue {
        val prefixed = if (value.text.startsWith(SENTINEL)) {
            value
        } else {
            value.copy(
                text = SENTINEL + value.text,
                selection = TextRange(
                    value.selection.start + SENTINEL.length,
                    value.selection.end + SENTINEL.length,
                ),
            )
        }
        return prefixed.copy(
            selection = TextRange(
                prefixed.selection.start.coerceAtLeast(SENTINEL.length),
                prefixed.selection.end.coerceAtLeast(SENTINEL.length),
            ),
        )
    }
}

@Composable
internal fun rememberBulletListEditorState(
    initialTexts: List<String>,
): BulletListEditorState = remember { BulletListEditorState(initialTexts) }
