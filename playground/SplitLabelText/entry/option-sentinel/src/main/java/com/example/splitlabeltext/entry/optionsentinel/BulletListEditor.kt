package com.example.splitlabeltext.entry.optionsentinel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.splitlabeltext.entry.optionsentinel.ui.BulletTextField

/**
 * [state]의 항목 목록을 LazyColumn 으로 그린다.
 *
 * - 항목별 입력은 [state]로 위임한다.
 * - 상태 생성은 호출부([OptionSentinelScreen])가, 렌더링은 이 컴포저블이 한다.
 */
@Composable
internal fun BulletListEditor(
    state: BulletListEditorState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(state.itemStates, key = { it.id }) { itemState ->
            BulletItem(
                itemState = itemState,
                shouldFocus = state.pendingFocusId == itemState.id,
                onValueChange = { state.onTextChange(itemState, it) },
                onFocusApplied = { state.clearPendingFocus() },
            )
        }
    }
}

/**
 * 한 항목의 행. [shouldFocus]가 서면 자기 [FocusRequester]로 입력 필드에 포커스를 준다.
 *
 * 포커스 로직을 상위가 아니라 이 컴포저블에 두는 이유:
 * - `requestFocus()`는 `Modifier.focusRequester(...)`가 걸린 입력 필드가 컴포지션에 올라와 배치까지 끝나 있어야 동작한다. 아니면 `IllegalStateException`.
 * - 분할 직후 [BulletListEditorState.pendingFocusId]가 새 항목을 가리킬 때, 그 항목의 행은 아직 컴포즈 전이다. 그래서 [BulletListEditor]나 [BulletListEditorState]에서 바로 부르면 이르다.
 * - 이 [LaunchedEffect]는 행이 컴포즈된 뒤 실행되므로, 그때는 입력 필드가 이미 배치돼 있다.
 */
@Composable
private fun BulletItem(
    itemState: BulletItemState,
    shouldFocus: Boolean,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusApplied: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(shouldFocus) {
        if (shouldFocus) {
            focusRequester.requestFocus()
            onFocusApplied()
        }
    }
    BulletTextField(
        value = itemState.value,
        onValueChange = onValueChange,
        modifier = Modifier.focusRequester(focusRequester),
    )
}
