package com.example.swemobulletedlist.entry.optionstatedashbullet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

private const val FIELD_TAG = "bulletListTextField"

/** 맨 앞 sentinel([rememberBulletListTextFieldState]가 붙이는 문자)을 포함한 저장값. 일반 문단 하나다. */
private const val STORED_TEXT = "​item"

/** 알약 색. 배경·글자와 헷갈리지 않는 색으로 둔다. */
private val PILL_COLOR = Color(0xFFFF0000)

/** 필드 배경색. */
private val BACKGROUND = Color.White

/**
 * 대시 불릿이 알약 색으로, 정한 자리에 그려지는지 캡처한 픽셀로 확인한다.
 *
 * - 대시 가운데 픽셀은 알약 색이다.
 * - 대시 위(두께만큼 위)와 대시와 글자 사이 간격은 배경색이다. 대시가 정한 크기보다 크게 그려지지 않는다.
 */
@RunWith(AndroidJUnit4::class)
class DashBulletTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun dashIsDrawnInPillColorLeftOfText() {
        val textStyle = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, color = Color.Black)
        var getLayout: (() -> TextLayoutResult?)? = null
        composeRule.setContent {
            BulletListTextField(
                state = TextFieldState(STORED_TEXT),
                modifier = Modifier.fillMaxWidth().background(BACKGROUND).testTag(FIELD_TAG),
                textStyle = textStyle,
                onTextLayout = { getLayout = it },
                pillStyle = PillStyle(color = PILL_COLOR),
            )
        }
        composeRule.waitForIdle()

        val layout = requireNotNull(getLayout?.invoke())
        val dash = DashBulletStyle().toMetrics(textStyle, composeRule.density)
        val textStartX = layout.getHorizontalPosition(0, usePrimaryDirection = true)
        val centerY = (layout.getLineTop(0) + layout.getLineBottom(0)) / 2
        val dashCenterX = textStartX - dash.gap - dash.length / 2

        val pixels = composeRule.onNodeWithTag(FIELD_TAG).captureToImage().toPixelMap()
        fun pixelAt(x: Float, y: Float) = pixels[x.roundToInt(), y.roundToInt()]

        assertEquals("대시 가운데", PILL_COLOR, pixelAt(dashCenterX, centerY))
        assertEquals("대시 위", BACKGROUND, pixelAt(dashCenterX, centerY - dash.thickness * 2))
        assertNotEquals("대시와 글자 사이", PILL_COLOR, pixelAt(textStartX - dash.gap / 2, centerY))
    }
}
