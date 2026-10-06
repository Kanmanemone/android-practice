package com.example.splitlabeltext.entry.optionsentinel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * 눈에 보이지 않는 sentinel 문자(zero-width space, U+200B).
 *
 * - 각 항목의 맨 앞에 넣는다.
 * - Android는 "빈 필드에서 Backspace"를 키 이벤트로 제공하지 않는다.
 * - 대신 항목 맨 앞에 sentinel을 항상 하나 깔아두면, "빈 필드에서 Backspace"라는 이벤트를 간접적으로 관찰할 수 있다.
 *     - TextField의 onValueChange 안에서 관찰한다.
 */
internal const val SENTINEL = "​"

/**
 * 불릿 리스트 속 1개의 아이템에 대응하는 State.
 *
 * - [value]의 text 는 반드시 [SENTINEL]로 시작한다는 불변식을 가진다.
 *
 * @property id LazyColumn 등에서 key로 사용됨.
 */
internal class BulletItemState(
    val id: Long,
    initialText: String,
) {
    var value by mutableStateOf(
        TextFieldValue(
            text = SENTINEL + initialText,
            selection = TextRange(SENTINEL.length + initialText.length),
        )
    )

    /** sentinel 을 제외한, 사용자에게 실제로 보이는 텍스트. */
    val visibleText: String get() = value.text.substring(SENTINEL.length)
}
