package com.example.splitlabeltext.entry.optionsentinel

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/** 상태([BulletListEditorState])를 만들어 컴포넌트([BulletListEditor])에 넘기는 역할만 한다. */
@Composable
fun OptionSentinelScreen(
    modifier: Modifier = Modifier,
    initialTexts: List<String> = listOf("Sample Text"),
) {
    val state = rememberBulletListEditorState(initialTexts)
    BulletListEditor(state = state, modifier = modifier)
}

@Preview(name = "빈 목록", showBackground = true)
@Composable
private fun OptionSentinelScreenEmptyPreview() {
    OptionSentinelScreen(initialTexts = listOf(""))
}

@Preview(name = "항목 여러 개", showBackground = true)
@Composable
private fun OptionSentinelScreenFilledPreview() {
    OptionSentinelScreen(initialTexts = listOf("우유 사기", "계란 사기", "빵 사기"))
}
