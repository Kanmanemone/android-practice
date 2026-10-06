package com.example.splitlabeltext.ui.info

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.splitlabeltext.entry.api.MyNavKey

/** [MyNavKey.description]을 여는 정보 아이콘 버튼. topBar와 옵션 목록이 같이 쓴다. */
@Composable
fun NavKeyInfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "설명 보기",
        )
    }
}

/**
 * [key]의 제목과 설명을 보여주는 다이얼로그.
 *
 * - 설명이 길어도 잘리지 않도록 본문을 세로 스크롤로 감싼다.
 */
@Composable
fun NavKeyInfoDialog(
    key: MyNavKey,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("확인") }
        },
        title = { Text(key.title) },
        text = {
            Text(
                text = key.description,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        },
    )
}
