package com.example.swemobulletedlist.entry.optionstatepillbullet

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 알약 라벨과 본문 사이의 구분자. 문단 맨 앞이 `라벨: `이면 그 문단은 알약 문단이다. */
internal const val PILL_DELIMITER = ": "

/**
 * 문단 맨 앞 알약의 범위.
 *
 * - 알약은 저장하지 않는다. 텍스트에서 매번 [pillRangeOf]로 다시 찾는다. 범위를 따로 추적하지 않아 undo·복사·저장에 손이 안 간다.
 * - offset은 저장값과 화면 텍스트에서 같다.
 *
 * @property labelStart 라벨 첫 글자.
 * @property labelEnd 라벨 바로 뒤. 곧 구분자(`:`) 위치다.
 */
internal class PillRange(val labelStart: Int, val labelEnd: Int) {
    /** 구분자(`: `) 바로 뒤. 본문이 시작하는 위치다. */
    val delimiterEnd: Int get() = labelEnd + PILL_DELIMITER.length

    /** 알약 배경이 덮는 범위의 끝. 라벨과 `:`까지이고, 공백은 알약과 본문 사이 간격이라 빠진다. */
    val backgroundEnd: Int get() = labelEnd + 1
}

/**
 * 문단(`start` ~ `end`, `end`는 문단 끝 직전)의 맨 앞에서 알약을 찾는다. 없으면 `null`이다.
 *
 * - 첫 문단은 맨 앞 sentinel을 건너뛰고 본다.
 * - 문단에서 처음 나온 `:`까지가 라벨이다. 라벨이 비면 알약이 아니다.
 * - `:` 바로 뒤에 공백까지 있어야 알약이다. `사과:`만 쳤을 때는 아직 알약이 아니고, 공백을 치는 순간 알약이 된다.
 */
internal fun CharSequence.pillRangeOf(start: Int, end: Int): PillRange? {
    val labelStart = paragraphContentStart(start)
    val colon = indexOf(PILL_DELIMITER[0], startIndex = labelStart)
    if (colon < 0 || colon >= end || colon == labelStart) return null
    if (colon + PILL_DELIMITER.length > end || !regionMatches(colon, PILL_DELIMITER, 0, PILL_DELIMITER.length)) return null
    return PillRange(labelStart, colon)
}

/**
 * 알약 모양과 알약 문단의 들여쓰기 설정.
 *
 * @property color 알약 배경색.
 * @property restLineIndent 알약 문단에서 넘친 줄의 들여쓰기(r).
 * @property overflow 알약 배경이 텍스트 영역 왼쪽 밖으로 넘치는 폭.
 *     - 넘친 부분은 decorator나 호출부의 padding 위에 그려진다. 배경만 넘고, 터치되지 않는다.
 *     - 이 필드를 감싼 부모가 clip하면 거기서 잘린다.
 */
@Immutable
internal data class PillStyle(
    val color: Color = Color.LightGray,
    val restLineIndent: PillRestLineIndent = PillRestLineIndent.Bullet,
    val overflow: Dp = 0.dp,
)

/** 알약 문단에서 넘친 줄을 어디에 맞출지. */
internal enum class PillRestLineIndent {
    /** 일반 문단의 글자 시작선(g). 문단 사이의 왼쪽 선이 맞는다. */
    Bullet,

    /** 알약 뒤 본문의 시작선. 알약이 문단 왼쪽에 단독으로 선다. 알약 폭은 layout 전에 따로 잰다. */
    Pill,
}
