package com.example.swemobulletedlist.entry.testbed

import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 라벨과 본문 사이에 오는 `: `. 문단 맨 앞이 `라벨: `이면 그 문단은 Pill 문단이다. */
internal const val PILL_DELIMITER = ": "

/**
 * Pill 문단에서 라벨 뒤 `:`에 거는 스타일.
 *
 * - 투명하게 칠해 숨긴다. 글자는 그대로 남아 폭을 차지한다.
 * - 투명한 `:`가 Pill 오른쪽 안쪽 여백이 된다.
 */
private val PILL_COLON_STYLE = SpanStyle(color = Color.Transparent)

/**
 * Pill 모양.
 *
 * - Pill 크기와 자리는 글자에서 정해진다. 라벨 앞뒤로 투명한 `:` 폭만큼 여백을 둔다.
 *
 * @property color Pill 배경색.
 * @property verticalInset 배경을 줄 상자보다 위아래로 줄이는 폭. 위아래 줄의 Pill끼리 붙어 보이지 않게 한다.
 */
@Immutable
internal data class PillStyle(
    val color: Color = Color.LightGray,
    val verticalInset: Dp = 2.dp,
)

/* ───────────── 판정 ───────────── */

/**
 * 문단 맨 앞 Pill의 범위.
 *
 * - Pill은 저장하지 않는다. 텍스트에서 매번 [pillRangeOf]로 다시 찾는다. 범위를 따로 추적하지 않아 undo·복사·저장에 손이 안 간다.
 *
 * @property labelStart 라벨 첫 글자.
 * @property labelEnd 라벨 바로 뒤. 곧 `:` 위치다.
 */
internal class PillRange(val labelStart: Int, val labelEnd: Int) {
    /** Pill 배경이 덮는 범위의 끝. 라벨과 `:`까지이고, 공백은 Pill과 본문 사이 간격이라 빠진다. */
    val backgroundEnd: Int get() = labelEnd + 1
}

/**
 * 문단(`start` ~ `end`)의 맨 앞에서 Pill을 찾는다. 없으면 `null`이다.
 *
 * - `start`, `end`는 `forEachParagraph`가 넘기는 값 그대로다.
 * - 문단에서 처음 나온 `:`까지가 라벨이다. 라벨이 비면 Pill이 아니다.
 * - `:` 바로 뒤에 공백까지 있어야 Pill이다. `사과:`만 쳤을 때는 아직 Pill이 아니고, 공백을 치는 순간 Pill이 된다.
 */
internal fun CharSequence.pillRangeOf(start: Int, end: Int): PillRange? {
    val colon = indexOf(':', startIndex = start)
    if (colon !in start + 1 until end) return null // `:`가 없거나, 다음 문단에 있거나, 라벨이 비었다
    if (!startsWith(PILL_DELIMITER, startIndex = colon)) return null // `:` 뒤에 공백이 없다
    return PillRange(start, colon)
}

/* ───────────── 화면용 텍스트 ───────────── */

/**
 * Pill 문단마다 라벨 뒤 `:`를 투명하게 칠한다. OutputTransformation 안에서 부른다.
 *
 * - 글자 색만 바꿔 레이아웃은 그대로다.
 */
internal fun TextFieldBuffer.hidePillColons() {
    val text = originalText
    forEachParagraph(text) { start, end ->
        val pill = text.pillRangeOf(start, end) ?: return@forEachParagraph
        addStyle(PILL_COLON_STYLE, pill.labelEnd, pill.backgroundEnd)
    }
}

/* ───────────── 그리기 ───────────── */

/**
 * Pill 문단마다 라벨 둘레에 둥근 배경을 그린다.
 *
 * - 글자는 건드리지 않고 배경만 그린다. 레이아웃이 일반 문단과 같아, Pill이 생겨도 글자도 커서도 움직이지 않는다.
 *     - `BasicTextField`는 `InlineTextContent`를 지원하지 않아 진짜 composable을 넣을 수 없다. 그래서 그린다.
 * - [textOrigin]은 그리기 좌표에서 본 텍스트 레이아웃의 왼쪽 위다. [layout]의 좌표에 더해 그린다.
 * - 가로: 라벨 앞뒤로 투명한 `:` 폭만큼 여백을 둔다. 라벨 시작 x - `:` 폭부터 `:` 뒤까지다.
 *     - 오른쪽 여백은 투명한 `:` 자체다. 왼쪽 여백도 같은 폭이라 라벨이 가운데에 온다.
 *     - `:` 뒤 공백은 덮지 않는다. Pill과 본문 사이에 한 칸 간격이 남는다.
 * - 세로: 줄 상자에서 위아래로 [PillStyle.verticalInset]만큼 줄인 높이. 모서리는 반원이다.
 * - 라벨이 줄을 넘치면 줄마다 따로 그린다.
 */
internal fun DrawScope.drawPills(
    layout: TextLayoutResult,
    style: PillStyle,
    textOrigin: Offset,
) {
    val text = layout.layoutInput.text
    val inset = style.verticalInset.toPx()
    forEachParagraph(text) { start, end ->
        // Pill 문단만 고르기
        val pill = text.pillRangeOf(start, end) ?: return@forEachParagraph

        // 가로 위치에 필요한 값 구하기
        val colonLeftPx = layout.getHorizontalPosition(pill.labelEnd, usePrimaryDirection = true) // `:` 왼쪽 경계의 x
        val colonRightPx = layout.getHorizontalPosition(pill.backgroundEnd, usePrimaryDirection = true) // `:` 오른쪽 경계의 x
        val horizontalPadding = colonRightPx - colonLeftPx // Pill 좌우 안쪽 여백 = `:`의 폭

        // 라벨이 걸치는 줄의 범위 도출
        val firstLine = layout.getLineForOffset(pill.labelStart)
        val lastLine = layout.getLineForOffset(pill.labelEnd) // lastLine == `:`가 있는 줄

        for (line in firstLine..lastLine) { // 줄마다 둥근 사각형 그리기 (Pill 그리기)
            // 왼쪽 끝
            val left = if (line == firstLine) {
                layout.getHorizontalPosition(pill.labelStart, usePrimaryDirection = true) - horizontalPadding
            } else {
                layout.getHorizontalPosition(layout.getLineStart(line), usePrimaryDirection = true)
            }
            // 오른쪽 끝
            val right = if (line == lastLine) colonRightPx else layout.getLineRight(line)
            // 위쪽과 높이
            val top = layout.getLineTop(line) + inset
            val height = layout.getLineBottom(line) - inset - top
            // 그리기
            drawRoundRect(
                color = style.color,
                topLeft = Offset(left, top) + textOrigin,
                size = Size(right - left, height),
                cornerRadius = CornerRadius(height / 2),
            )
        }
    }
}
