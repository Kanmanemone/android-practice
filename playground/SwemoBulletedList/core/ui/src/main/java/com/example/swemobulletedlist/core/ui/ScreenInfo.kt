package com.example.swemobulletedlist.core.ui

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import com.example.swemobulletedlist.entry.api.EntryKey

@Composable
fun ScreenInfoDialog(
    key: EntryKey,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("확인") }
        },
        title = { Text(key.title) },
        text = {
            val linkColor = MaterialTheme.colorScheme.primary
            val description = remember(key.description, linkColor) {
                linkifyUrls(key.description, linkColor)
            }
            Text(
                text = description,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        },
    )
}

@Composable
fun ScreenInfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = "설명 보기",
        )
    }
}

private val UrlRegex = Regex("""https?://\S+""")

private fun linkifyUrls(text: String, linkColor: Color): AnnotatedString = buildAnnotatedString {
    val linkStyles = TextLinkStyles(
        style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
    )
    var cursor = 0
    UrlRegex.findAll(text).forEach { match ->
        append(text.substring(cursor, match.range.first))
        withLink(LinkAnnotation.Url(match.value, linkStyles)) {
            append(match.value)
        }
        cursor = match.range.last + 1
    }
    append(text.substring(cursor))
}
