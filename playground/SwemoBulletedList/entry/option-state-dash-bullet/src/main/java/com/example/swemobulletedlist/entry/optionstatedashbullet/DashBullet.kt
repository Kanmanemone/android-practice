package com.example.swemobulletedlist.entry.optionstatedashbullet

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp

/** [TextStyle]에 글자 크기가 없을 때 Compose가 쓰는 크기. `BasicTextField`의 기본 글자 크기와 같다. */
private val DEFAULT_FONT_SIZE = 14.sp

/**
 * 대시 불릿 모양. 일반 문단 앞에 그리는 짧고 둥근 가로 막대다.
 *
 * - 크기는 글자 크기 단위(em)로 정한다. 글자 크기나 시스템 글꼴 배율이 바뀌어도 글자와 같은 비율로 커진다.
 *     - sp로 줘도 된다. 그때는 글자 크기와 상관없는 고정 크기다.
 * - 색은 알약 배경색([PillStyle.color])을 쓴다. 알약과 대시가 한 쌍으로 보인다.
 *
 * @property length 막대 길이. `-` 글자(약 0.3em)보다 조금 길다.
 * @property thickness 막대 두께. `-` 글자(약 0.07em)보다 조금 두껍다. 양 끝은 두께의 반지름으로 둥글다.
 * @property gap 막대 오른쪽 끝과 글자 사이 간격.
 */
@Immutable
internal data class DashBulletStyle(
    val length: TextUnit = 0.5.em,
    val thickness: TextUnit = 0.14.em,
    val gap: TextUnit = 0.5.em,
)

/**
 * [DashBulletStyle]을 px로 바꾼 값.
 *
 * @property length 막대 길이.
 * @property thickness 막대 두께.
 * @property gap 막대 오른쪽 끝과 글자 사이 간격.
 */
internal class DashBulletMetrics(val length: Float, val thickness: Float, val gap: Float) {
    /** 대시가 차지하는 들여쓰기 폭. 막대 길이 + 간격이다. */
    val indent: Float get() = length + gap
}

/** [textStyle]의 글자 크기를 기준으로 [DashBulletStyle]을 px로 바꾼다. */
internal fun DashBulletStyle.toMetrics(textStyle: TextStyle, density: Density): DashBulletMetrics {
    val fontSize = textStyle.fontSize.takeIf { it.isSpecified } ?: DEFAULT_FONT_SIZE
    val fontSizePx = with(density) { fontSize.toPx() }
    fun TextUnit.resolvePx(): Float = if (isEm) value * fontSizePx else with(density) { toPx() }
    return DashBulletMetrics(length = length.resolvePx(), thickness = thickness.resolvePx(), gap = gap.resolvePx())
}
