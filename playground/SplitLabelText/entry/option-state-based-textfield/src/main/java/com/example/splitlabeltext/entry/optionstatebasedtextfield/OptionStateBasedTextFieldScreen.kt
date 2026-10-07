package com.example.splitlabeltext.entry.optionstatebasedtextfield

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.byValue
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OptionStateBasedTextFieldScreen(
    modifier: Modifier = Modifier,
    initialLabel: String = "",
    initialText: String = "",
) {
    val labelState = rememberTextFieldState(initialLabel)
    val textState = rememberTextFieldState(initialText)

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            state = labelState,
            label = { Text("Label") },
            modifier = Modifier.widthIn(max = 120.dp),
            inputTransformation = MyInputTransformation,
            outputTransformation = MyOutputTransformation,
        )
        OutlinedTextField(
            state = textState,
            label = { Text("Text") },
            modifier = Modifier.weight(1f),
            inputTransformation = MyInputTransformation,
            outputTransformation = MyOutputTransformation,
        )
    }
}

/**
 * 입력을 손대지 않고 그대로 [TextFieldState]에 반영하는 [InputTransformation].
 *
 * - 사용자가 입력한 직후, 상태에 커밋되기 전에 불린다.
 * - `current`는 입력 전 값, `proposed`는 입력이 반영된 값이다. 반환한 값이 상태에 저장된다.
 * - 지금은 `accepted`에 `proposed`를 그대로 담아 돌려주므로 입력이 그대로 저장되고, 커서도 그대로 유지된다.
 */
private val MyInputTransformation = InputTransformation.byValue { current, proposed ->
    val accepted = proposed
    accepted
}

/**
 * 상태 값을 손대지 않고 그대로 화면에 보여주는 [OutputTransformation].
 *
 * - 화면에 그리기 직전에 불린다. 여기서 버퍼를 고치면 보이는 값만 바뀌고 [TextFieldState]의 값은 그대로다.
 * - 버퍼에는 처음부터 상태 값(`original`)이 들어 있다. `transformed`를 바꾸면 그 값이 화면에 나간다.
 * - 값이 같을 때 덮어쓰면(`replace(0, length, ...)`) 전체가 바뀐 것으로 기록돼 커서 위치 매핑이 틀어질 수 있다. 그래서 달라졌을 때만 덮어쓴다.
 */
private val MyOutputTransformation = OutputTransformation {
    val original = toString()
    val transformed = original
    if (transformed != original) {
        replace(0, length, transformed)
    }
}

@Preview(name = "text 비어있음", showBackground = true)
@Composable
private fun OptionStateBasedTextFieldScreenEmptyTextPreview() {
    OptionStateBasedTextFieldScreen(initialLabel = "이름", initialText = "")
}

@Preview(name = "text 채워짐", showBackground = true)
@Composable
private fun OptionStateBasedTextFieldScreenFilledTextPreview() {
    OptionStateBasedTextFieldScreen(initialLabel = "이름", initialText = "홍길동")
}

@Preview(name = "label 빈 값", showBackground = true)
@Composable
private fun OptionStateBasedTextFieldScreenEmptyLabelPreview() {
    OptionStateBasedTextFieldScreen(initialLabel = "", initialText = "홍길동")
}
