# option-state-pill-bullet 테스트 코드와 작업 기록 — 알약 배치 · 클립보드 · 커서 · 탭

`feature/pill-bullet` 브랜치에서 `f25d7dd` 다음부터 한 작업과 테스트 코드를 정리한다.

- 같은 코드를 옮겨 온 `option-state-dash-bullet`([3])에도 같은 수정과 테스트가 들어 있다.
무엇을 바꿨는지, 왜 그렇게 했는지, 중간에 실패한 방법과 그 원인, 테스트를 어떻게 짜고 돌리는지를 담는다.

---

## 1. 한눈에 보기

| 주제 | 요구 | 최종 상태 |
|---|---|---|
| 커서 고정 | `xxx:` 뒤에 공백을 쳐서 알약이 생겨도 커서가 튀지 않아야 한다 | 본문 시작 x를 일반 문단일 때와 같게 맞춘다. 실측 오차 0.01px 미만 |
| 알약 범위 | 알약은 `xxx:`까지만 덮고, 본문과 공백 한 칸을 띄운다 | `:` 뒤까지만 배경을 그린다. 공백은 원래 폭 그대로 |
| 라벨 가운데 정렬 | 알약 안 라벨이 가운데에 와야 한다 | 왼쪽·오른쪽 여백 차이 최대 1px (Android가 들여쓰기를 정수 px로만 받기 때문) |
| 복사·잘라내기 | 붙여넣기 → 잘라내기 → 다시 붙여넣기 해도 화면이 같아야 한다 | 줄바꿈이 공백으로 바뀌던 버그를 고쳤다. 테스트로 확인한다 |
| 원본 보존 | 화면 처리 때문에 저장값이 바뀌면 안 된다 | 화면 처리는 OutputTransformation 안에서만 한다. 테스트로 확인한다 |
| 메모장 같은 커서 | 본문에서 알약 쪽으로 드래그해도 커서가 순간이동하지 않아야 한다 | 알약 안 커서 규칙을 없앴다. 라벨도 일반 글자처럼 커서·선택된다. 테스트로 확인한다 |
| 문단 탭 | 문단 오른쪽 빈 곳을 누르면 그 문단에 커서가 와야 한다 | 다음 문단 맨 앞으로 가던 버그를 고쳤다. 테스트로 확인한다 |

이 세션의 커밋(오래된 것부터):

| 커밋 | 내용 |
|---|---|
| `0c294dd` | 알약이 생겨도 커서가 움직이지 않게 하고, 라벨 가운데 정렬 첫 시도(자간 사용) |
| `db2dd37` | 알약 배경을 `라벨:`까지만 그려 본문과 한 칸 띄움 |
| `d96959c` | 자간을 `:` 한 글자에만 걸어 가운데 정렬 |
| `5990e6b` | 가운데 정렬 계산에 알약 넘침 폭(`overflow`) 반영 |
| `f9beea0` | 자간 값을 공식이 아니라 실제로 재서 구함 |
| `f5e9de6` | 자간 대신 가로 배율(`scaleX`)로 `:` 폭을 맞춤. 본문과의 간격이 다시 한 칸이 됨 |
| `ef2cd2f` | 복사·잘라내기 때 문단 사이 줄바꿈이 공백으로 바뀌던 버그 수정 (`StoredTextClipboard`) |
| `45575fb` | 붙여넣기 → 잘라내기 → 다시 붙여넣기 화면 동일성 테스트 (`PasteCutRoundTripTest`) |
| `f0185ba` | 알약 배치를 라벨마다 재고, 정수 들여쓰기까지 맞춰 계산 (`measurePillLayout`) |
| `e163635` | 알약 배치 규칙 테스트 (`PillLayoutTest`) |
| `acf4e7f` | 알약 안 커서 규칙(`KeepCursorOutOfPill`) 제거, 커서 테스트 (`PillCursorTest`) |
| `154bcd7` | 문단 오른쪽 빈 곳을 누르면 커서가 다음 문단 맨 앞으로 가던 버그 수정 (`ParagraphEndTapFix`), 탭 테스트 (`ParagraphTapTest`) |

---

## 2. 용어

| 기호 | 뜻 |
|---|---|
| g | 일반 문단 들여쓰기. 불릿이 차지하는 폭. `bulletIndent` 파라미터, 미지정이면 `"•  "` 폭 |
| p | 알약 문단 첫 줄 들여쓰기. 라벨 글자가 시작하는 x이자 알약 왼쪽 안쪽 여백 |
| 넘침 폭 | `PillStyle.overflow`. 알약 배경이 텍스트 영역 왼쪽 밖으로 더 그려지는 폭. 화면에서는 8dp |
| 본문 시작 | 구분자 `: ` 뒤 첫 글자의 x. 공백을 친 직후 커서 자리 |
| 왼쪽 여백 | 넘침 폭 + p. 알약 왼쪽 끝부터 라벨 글자까지 |
| 오른쪽 여백 | 투명한 `:`의 폭. 라벨 글자 끝부터 알약 오른쪽 끝까지 |
| 저장값 | `TextFieldState.text`. 사용자가 입력한 그대로의 글 (+ 맨 앞 sentinel) |
| 화면용 텍스트 | OutputTransformation을 거친 글. 문단 끝 `\n`이 공백으로 바뀌어 있다 |

---

## 3. 최종 설계

### 3.1 알약이 그려지는 방식 (변하지 않은 부분)

- 문단 맨 앞이 `라벨: `이면 알약 문단이다(`pillRangeOf`). 알약은 저장하지 않고 매번 텍스트에서 찾는다.
- 글자는 그대로 두고, OutputTransformation에서 `:`를 투명하게 칠한다. 배경은 decorator의 `drawBehind`에서 그린다.
- 알약 문단에는 불릿을 그리지 않는다. 알약이 불릿 자리(x = 0)에서 시작한다.

### 3.2 알약 배경 범위

- 가로: `-overflow`부터 `x(backgroundEnd)`까지. `backgroundEnd`는 `:` 바로 뒤다.
- 구분자 공백은 배경 밖이다. 알약과 본문 사이 간격으로 남는다.

### 3.3 두 조건과 계산 (`measurePillLayout`)

맞추는 조건은 두 개고, 앞의 것이 우선이다.

1. **커서 고정**: 본문 시작 = g + `라벨: ` 폭 (알약이 아닌 일반 문단으로 그렸을 때와 같은 자리)
2. **가운데 정렬**: 넘침 폭 + p = `:` 폭

본문 시작은 `p + 라벨 폭 + `:` 폭 + 공백 폭`이다. 정할 값은 p와 `:` 폭 두 개다.

```
p     = (본문 시작 - 라벨 폭 - 공백 폭 - 넘침 폭) / 2     // 두 조건을 함께 풀면 나온다
:폭   = 본문 시작 - p - 라벨 폭 - 공백 폭               // 조건 1을 정확히 맞춘다
```

- p는 Android가 정수 px로만 쓴다(3.4). 그래서 p를 정수로 고른 뒤, 조건 1은 `:` 폭으로 정확히 맞춘다. 남는 1px 안쪽 차이는 조건 2(가운데 정렬)에 남는다.
- `:` 폭은 가로 배율(`TextGeometricTransform.scaleX`)로 만든다. 배율은 재 보면서 고친다(`fitScaleX`).
    - 배율 1에서 시작해 `배율 *= 목표 폭 / 잰 폭`을 최대 4번 반복하고, 그동안 가장 가까웠던 배율을 고른다.
    - 글자 폭을 정수 px로 그리는 설정에서도 가장 가까운 값이 나온다.
- 넘침 폭이 너무 커서 p가 0보다 작으면 p = 0. 조건 1만 맞고 라벨은 오른쪽으로 치우친다.
- 결과는 라벨별로 `LruCache`(64개)에 기억한다. OutputTransformation은 글자를 칠 때마다 모든 문단에 돌기 때문이다. 글꼴·g·넘침 폭이 바뀌면 캐시를 통째로 새로 만든다.

### 3.4 Android가 들여쓰기를 쓰는 방식 (`toAppliedIndent`)

Compose(`ui-text` 1.12.1 `SpannableExtensions.android.kt`)의 `setTextIndent`는 이렇게 한다.

```kotlin
LeadingMarginSpan.Standard(ceil(firstLine).toInt(), ceil(restLine).toInt())
```

즉 `TextIndent`(sp) → px → **올림(ceil)한 정수**다. 그래서 이렇게 처리한다.

- `Density.toAppliedIndent(px) = ceil(px.toSp().toPx())`로 같은 과정을 따라 실제로 쓰일 값을 구한다.
- g도 실제로 쓰이는 정수 값으로 본문 시작을 계산한다.
- p는 목표에 가장 가까운 정수 n을 고르고 **n − 0.5를 넘긴다**.
    - 정수 n을 그대로 넘기면 sp를 거치는 변환 오차로 n보다 아주 조금 커져 n + 1로 올라갈 수 있다.
    - 실제로 이 폰(글꼴 크기 1.3배, Android 14 비선형 글꼴 배율)에서 23을 넘기면 24가 됐다.

### 3.5 잴 때의 모양을 화면과 같게

TextMeasurer로 미리 잰 값과 화면 값이 같으려면 글자 묶음(run)이 같은 자리에서 나뉘어야 한다.

- 잴 때도 `:`에 같은 스타일(배율)을 건다. 스타일 경계에서 run이 나뉘며 앞 글자 폭이 조금 바뀐다(실측 0.9px).
- 공백 뒤에 본문 글자 하나(`PILL_BODY_PROBE = "x"`)를 둔다.
    - 공백이 줄 끝이면 Android가 자간을 덜 붙여, 본문이 있을 때와 폭이 다르다(실측 0.9px).
    - 화면에 오래 남는 쪽(본문이 있을 때)에 맞춘다. 그래서 자간이 있는 글자 스타일에서는 `abc: `를 막 친 순간(본문 없음) 커서가 1px 미만 차이 날 수 있다.

### 3.6 OutputTransformation 인터페이스

`bulletListOutputTransformation { text, start, end -> ParagraphLayout }`

- 문단마다 `ParagraphLayout(indent, pillColonStyle)`을 받는다.
- `indent`는 `ParagraphStyle`로, `pillColonStyle`은 `:` 한 글자(`labelEnd until backgroundEnd`)에 건다.

---

## 4. 가운데 정렬이 여러 번 실패한 과정과 원인

같은 문제를 여러 번 고쳤다. 원인마다 다음 단계에서 드러났다. 같은 실수를 반복하지 않도록 남긴다.

### 4.1 첫 시도 — 자간으로 `: ` 폭 맞추기 (`0c294dd`)

- 처음에는 `: ` 두 글자에 `letterSpacing`을 걸어 폭을 (g + `: ` 폭) / 2로 맞췄다.
- 사용자 피드백 1: 알약이 공백까지 덮어 본문과 딱 붙어 답답하다 → `db2dd37`에서 배경을 `:`까지로 줄였다.
- 그러자 공백에 걸린 자간이 알약 밖으로 빠져 오른쪽 여백이 좁아졌다 → `d96959c`에서 자간을 `:`에만 걸었다.

### 4.2 넘침 폭 누락 (`5990e6b`)

- 알약 배경은 왼쪽으로 `overflow`(8dp)만큼 더 그려진다. 계산은 알약이 x = 0에서 시작한다고 봤다.
- 그래서 눈에 보이는 왼쪽 여백이 8dp 넓었다. 왼쪽 여백을 "넘침 폭 + 들여쓰기"로 고쳤다.

### 4.3 자간이 반씩 나뉘어 붙음 (`f9beea0`) — 실기기 측정으로 발견

폰을 연결해 그리기 코드에 임시 로그를 넣고 실측했다.

```
left=-22.5 labelStartX=24.0 labelEndX=117.6 colonBox=117.6..147.4 spaceBox=147.4..177.2
```

- 왼쪽 여백 46.5px, 오른쪽 여백 29.8px.
- `:`에만 자간 30px를 걸었는데 `:`는 14px, **뒤 공백이 15px** 넓어졌다. Android는 자간을 글자 앞뒤로 반씩 나눠 붙인다.
- 자간 값을 공식 대신 두 값으로 재 보고 직선으로 풀어 구하도록 바꿨다 → 35.5px / 35.4px로 맞았다.

### 4.4 알약과 본문 사이가 공백 두 칸처럼 보임 (`f5e9de6`)

- 4.3의 결과로 공백 폭이 15px → 35px가 되어 있었다(사용자 피드백 2).
- 자간 대신 **가로 배율(`scaleX`)** 로 바꿨다. 가로 배율은 그 글자 폭에만 곱해지고 옆 글자로 새지 않는다.
- 실측: 공백 15.8px, 왼쪽 44.5px / 오른쪽 45.8px.

### 4.5 기준 라벨 하나로 잰 값을 모든 라벨에 씀 (`f0185ba`)

- 배율을 기준 라벨 `"a"`로 한 번만 쟀다. 라벨마다 run 경계 효과가 달라 1px 넘게 어긋났다.
- 라벨마다 재서 캐시하도록 바꿨다.

### 4.6 정수 들여쓰기 (`f0185ba`) — 테스트로 발견

`PillLayoutTest`를 처음 돌렸을 때 이렇게 실패했다.

```
'abc' 왼쪽 여백 31.0 / 오른쪽 여백 30.0
```

- 위치가 모두 정수였다. 두 가지 원인이 겹쳐 있었다.
    1. Compose가 들여쓰기를 `ceil`로 정수 px로 올린다(3.4).
    2. 테스트의 기본 `TextStyle`은 `TextMotion.Static`이라 글자 폭도 정수 px로 그린다. 앱 화면은 테마 bodyLarge에 자간 0.5sp가 있어 소수 위치가 나온다.
- "폭 = 배율 × 원래 폭"이라는 계산이 정수 반올림 때문에 그대로 맞지 않아, 배율을 재 보면서 고치는 `fitScaleX`로 바꿨다.

### 4.7 공백에는 가로 배율이 먹지 않음 — 시도 후 되돌림

- 들여쓰기 반올림 오차를 공백 폭으로 흡수하려고 공백에도 배율을 걸어 봤다.
- 실측: 목표 14.5px → 실제 14.0px, 목표 16.61px → 실제 16.80px. 배율을 바꿔도 공백 폭이 그대로였다.
- 공백 배율을 없애고, 조건 1(커서)은 `:` 폭으로 정확히 맞추고 남는 차이는 조건 2(가운데)에 두는 설계로 정리했다.

### 4.8 비선형 글꼴 배율에서 n이 n + 1이 됨 — 테스트로 발견

- 가운데 정렬 오차가 이론상 최대 1px인데 1.7px가 나왔다.
- 로그로 확인: `target=23.15 req=23.0 p=24.0`. 23을 넘겼는데 24가 쓰였다.
- 글꼴 크기 1.3배(비선형 배율)에서 px → sp → px 변환이 23보다 아주 조금 커지고, `ceil`로 24가 된다.
- n − 0.5를 넘기도록 바꿔 해결했다 → 최대 오차 1.0px (이론상 한계).

### 최종 실측 (PillLayoutTest 전 조합, SM-A165N)

| 항목 | 최대 오차 |
|---|---|
| 가운데 정렬 (왼쪽 − 오른쪽 여백) | 1.0px |
| 본문 시작 (커서 고정) | 0.0078px |

---

## 5. 복사·잘라내기 버그 (`ef2cd2f`)

### 증상

`PasteCutRoundTripTest`를 처음 돌렸을 때 다시 붙여넣은 화면이 이랬다.

```
처음 붙여넣기:           xxx | aaaa
                        • dddddd
                        ffff | ssss

잘라내고 다시 붙여넣기:   xxx | aaaa dddddd ffff: ssss
```

세 문단이 한 문단으로 합쳐졌다. 사용자가 직접 복사·잘라내기를 해도 똑같이 생기는 실제 버그다.

### 원인

- Compose(`foundation` 1.12.1 `TextFieldSelectionState`)의 `copy()`/`cut()`은 **`textFieldState.visualText`**, 즉 화면용 텍스트를 클립보드에 넣는다.
- 화면용 텍스트는 문단 끝 `\n`이 `PARAGRAPH_BREAK`(공백)로 바뀌어 있다. 그래서 줄바꿈이 사라졌다.
- `\n`을 화면용 텍스트에서 그대로 둘 수는 없다. 스타일 범위가 갈리는 곳마다 빈 줄이 하나씩 더 생기기 때문이다(기존 설계 이유).

### 해결 — `StoredTextClipboard`

- 필드 안쪽에만 `LocalClipboard`를 감싼다. 그 안에서 클립보드에 쓰는 쪽은 이 필드의 복사·잘라내기뿐이다.
- 클립보드에 들어온 글을 저장값의 선택 부분과 대조한다. 화면용으로 바뀐 것이 맞을 때만 저장값으로 바꿔 넣는다.
    - 대조 규칙(`showsAs`): 길이가 같고, 다른 글자는 저장값의 `\n`이 화면에서 공백으로 바뀐 자리뿐이어야 한다.
    - 맞지 않으면 들어온 글을 그대로 쓴다. 엉뚱한 글로 바뀌는 일은 없다.
- 맨 앞 sentinel은 빼고 넣는다.
- 읽기(붙여넣기)는 원래 클립보드에 그대로 넘긴다.
- `AndroidClipboard`를 구현해, 플랫폼 클립보드를 꺼내 쓰는 코드도 그대로 동작한다.

### 처음 시도가 실패한 이유 — 잘라내기 순서

- 처음에는 "클립보드에 쓸 때 선택이 있으면 저장값의 선택 부분을 넣는다"로 만들었다. 테스트가 그대로 실패했다.
- Compose 1.12.1의 `cut()`은 **글자를 먼저 지우고(`cutWithResult`) 그다음 클립보드에 쓴다.** 쓰는 시점에는 선택이 이미 사라져 있었다.

```kotlin
suspend fun cut() {
    val cutValue = cutWithResult() ?: return   // 여기서 deleteSelectedText()
    clipboard.setClipEntry(cutValue.toClipEntry())
}
```

- 그래서 `editRecorder`(InputTransformation)를 `inputTransformation` 맨 끝에 붙였다. 편집마다 "편집 전 저장값과 선택"을 남긴다.
- 클립보드에 쓸 때 지금 선택(복사)과 직전 편집 전 선택(잘라내기)을 차례로 대조한다. 그래서 Compose가 순서를 바꿔도 동작한다.

---

## 6. 테스트

모듈에 instrumented 테스트 설정을 추가했다(`build.gradle.kts`).

```kotlin
defaultConfig { testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" }

androidTestImplementation(platform(libs.androidx.compose.bom))
androidTestImplementation(libs.androidx.compose.ui.test.junit4)
androidTestImplementation(libs.androidx.junit)
debugImplementation(libs.androidx.compose.ui.test.manifest)
```

### 실행

```
./gradlew :entry:option-state-pill-bullet:connectedDebugAndroidTest
```

- 기기나 에뮬레이터가 연결되어 있어야 한다.
- 결과 보고서: `entry/option-state-pill-bullet/build/reports/androidTests/connected/debug/index.html`

### 6.1 `PasteCutRoundTripTest`

파일: `src/androidTest/.../PasteCutRoundTripTest.kt`

**확인하는 것**: 아래 글을 붙여넣고, 전부 잘라내고, 다시 붙여넣어도 화면이 그대로인가.

```
xxx: aaaa
dddddd
ffff: ssss
```

**순서**

1. 실제 화면(`OptionStatePillBulletScreen`)을 빈 글로 띄운다. 테마 글꼴·알약 설정까지 실제와 같은 조건이다.
2. 시스템 클립보드에 글을 넣고, 필드를 탭해 커서를 맨 끝에 둔 뒤 `SemanticsActions.PasteText`.
3. 저장값과 필드 캡처(첫 화면)를 기록한다. 저장값이 붙여넣은 글로 끝나는지 확인한다.
4. 전체 선택(`performTextInputSelection(0..끝)`) 후 `SemanticsActions.CutText`. **클립보드 글이 원문과 같은지** 확인한다.
5. 다시 붙여넣고 저장값과 필드 캡처(두 번째 화면)를 기록한다.
6. 저장값이 같은지, 두 캡처가 **크기와 모든 픽셀까지** 같은지 확인한다. 다르면 다른 픽셀 수를 알려 준다.

**테스트를 짜며 부딪힌 점**

| 문제 | 원인 | 해결 |
|---|---|---|
| 붙여넣은 글이 없다고 실패 | semantics `EditableText`는 화면용 텍스트라 `\n`이 공백 | 저장값인 `InputText`를 읽는다 |
| 텍스트는 같은데 픽셀 27,016개가 다름 | 5장의 실제 버그 | `StoredTextClipboard`로 수정 |
| 캡처에 커서가 찍힘 | 포커스를 받을 곳이 필드 하나뿐이라 `clearFocus()`를 해도 다시 필드로 감 | 1dp짜리 포커스용 빈 칸(`focusSink`)을 두고 캡처 전에 포커스를 그쪽으로 옮긴다. `assertIsNotFocused()`로 확인 |
| 디버깅용 캡처 파일을 못 가져옴 | `connectedDebugAndroidTest`는 끝나면 테스트 APK를 지워 앱 데이터도 사라짐 | `installDebugAndroidTest` 후 `adb shell am instrument -w <패키지>.test/androidx.test.runner.AndroidJUnitRunner`로 직접 실행 |
| `adb pull` 경로가 `C:/dev/Git/storage/...`로 바뀜 | Git Bash(MSYS)가 `/`로 시작하는 인자를 Windows 경로로 바꿈 | `MSYS_NO_PATHCONV=1 adb pull ...` |

### 6.2 `PillLayoutTest`

파일: `src/androidTest/.../PillLayoutTest.kt`

**확인하는 것**: 알약 배치 규칙이 여러 조건에서 지켜지는가.

**조합** (2 × 3 × 2 = 12가지)

| 축 | 값 | 이유 |
|---|---|---|
| 글자 스타일 | 자간 없음 / 자간 0.5sp | 자간 없음은 글자 위치가 정수 px, 자간 있음(Material3 bodyLarge처럼)은 소수 px |
| 불릿 들여쓰기 g | 기본(`"•  "` 폭) / 24dp / 48dp | 불릿 모양이 바뀌면 g가 바뀐다. g를 바꾸는 것으로 불릿 교체를 대신한다 |
| 넘침 폭 | 0 / 8dp | 넘침 폭이 왼쪽 여백에 들어가는지 본다 |

**라벨**: `abc`, `iii`(좁은 글자), `WWW`(넓은 글자), `가나다`(한글), `Label 1`(공백 포함), `x`(한 글자). 각 라벨로 `라벨: body` 문단을 만들고, 맨 끝에 일반 문단 `plain`을 둔다.

**조합마다 확인하는 것**

| 항목 | 기준 | 허용 오차 |
|---|---|---|
| 가운데 정렬 | 넘침 폭 + x(라벨 시작) = x(`:` 뒤) − x(`:`) | 1px (`CENTER_TOLERANCE_PX`) |
| 커서 고정 | x(본문 시작) = g + (같은 문단을 일반 문단으로 TextMeasurer로 잰 본문 시작) | 0.05px (`BODY_START_TOLERANCE_PX`) |
| 저장값 보존 | `state.text` = 처음 넣은 글 | 완전 일치 |
| 알약 수 | 라벨 수와 같다 | 완전 일치 |

- g는 구현에서 가져오지 않고, 화면의 일반 문단 `plain`의 글자 시작 x에서 읽는다. 구현 내부 값에 기대지 않기 위해서다.
- 한 번의 `setContent` 안에서 `mutableStateOf`로 조합을 바꿔 가며 다시 그린다. `onTextLayout`으로 실제 `TextLayoutResult`를 받아 확인한다.
- 넘침 폭이 너무 커서 가운데 정렬이 불가능한 조합(p < 0)은 넣지 않았다.

---

## 7. 질문별 답 정리

1. **원본 텍스트를 훼손하나?** 아니다. 알약, `:` 숨기기, 줄바꿈 처리는 모두 OutputTransformation 안의 화면용 텍스트에서만 한다. 저장값에 끼어드는 건 원래 설계의 맨 앞 sentinel뿐이고, 복사할 때 뺀다. `PillLayoutTest`(저장값 보존)와 `PasteCutRoundTripTest`(잘라낸 글 = 원문)가 지킨다.
2. **하드코딩이나 주먹구구식 코드가 있나?** 기준 라벨 `"a"` 하나로 잰 값을 모든 라벨에 쓰던 부분이 있었다. 라벨마다 재서 캐시하도록 고쳤다. 남은 상수는 이렇다.
    - `PILL_BODY_PROBE = "x"`: 공백을 줄 끝이 아니게 하는 용도라 어떤 글자든 된다.
    - `fitScaleX`의 반복 횟수(4)와 허용 차이(0.01px).
    - 배율 하한(0.01), 캐시 크기(64).
3. **불릿이 바뀌어도 되나?** 된다. 계산은 불릿 모양이 아니라 g만 쓴다. `PillLayoutTest`가 g 세 가지로 확인한다.
4. **좌우 여백을 완전히 같게 할 수 있나?** 1px 아래로는 늘 맞출 수 없다. Android가 들여쓰기를 정수 px로만 받아 왼쪽 여백이 1px 단위로만 움직이기 때문이다. 커서 고정을 우선해 정확히 맞추고, 가운데 정렬은 최대 1px로 둔다.

---

## 8. 남은 제약

- 가운데 정렬은 최대 1px 차이가 날 수 있다(4장 최종 실측).
- 자간이 있는 글자 스타일에서 `abc: `를 막 친 순간(본문이 아직 없을 때) 커서가 1px 미만 움직일 수 있다(3.5).
- 넘침 폭이 g + `:` 폭보다 크면 가운데 정렬을 포기하고 커서 고정만 지킨다.
- 라벨이 줄을 넘쳐 여러 줄이 되면 가운데 정렬하지 않는다.
- `PillRestLineIndent.Pill`(넘친 줄을 본문 시작선에 맞춤)은 본문 시작을 정수로 올려 쓰므로 1px 안쪽 차이가 날 수 있다.
- 시각적 가운데는 커서 위치 기준이다. 글자 모양(글자 양옆의 빈 여백)에 따라 눈으로는 1~2px 치우쳐 보일 수 있다.

---

## 9. 파일 지도

| 파일 | 이번 작업에서 한 일 |
|---|---|
| `src/main/.../BulletListTextField.kt` | `measurePillLayout`, `fitScaleX`, `toAppliedIndent`, `PillLayout`, `pillColonStyle`, 라벨별 `LruCache`, 알약 배경 범위, `StoredTextClipboard` 연결 |
| `src/main/.../BulletListTransformation.kt` | `bulletListOutputTransformation`이 `ParagraphLayout`을 받도록 변경, `KeepCursorOutOfPill` 제거 |
| `src/main/.../StoredTextClipboard.kt` | 새 파일. 복사·잘라내기 때 저장값을 넣는 클립보드 |
| `src/main/.../Pill.kt` | `PillStyle.startPadding` 제거(들여쓰기가 규칙으로 정해짐), `PillRange.backgroundEnd` |
| `src/main/.../OptionStatePillBulletKey.kt` | 화면 설명 문구 |
| `src/androidTest/.../PasteCutRoundTripTest.kt` | 새 파일 |
| `src/androidTest/.../PillLayoutTest.kt` | 새 파일 |
| `src/androidTest/.../PillCursorTest.kt` | 새 파일 |
| `src/main/.../ParagraphEndTapFix.kt` | 새 파일. 문단 오른쪽 탭 보정 |
| `src/androidTest/.../ParagraphTapTest.kt` | 새 파일 |
| `build.gradle.kts` | instrumented 테스트 설정 |

---

## 10. 알약 안 커서 규칙 제거 (`acf4e7f`)

### 증상

본문에서 알약 쪽으로 드래그하면 선택 끝이 라벨 앞이나 본문 시작으로 순간이동했다. 라벨 글자 사이를 탭해도 커서가 본문 시작으로 갔다.

### 원인

`KeepCursorOutOfPill`(InputTransformation)이 커서·선택 끝이 알약 안(라벨 첫 글자 뒤 ~ 구분자 공백 앞)에 오면 바깥으로 밀어냈다. 드래그·탭·방향키로 바뀌는 선택도 모두 이 규칙을 지난다.

### 해결

- 규칙과 헬퍼(`offsetOutOfPill`)를 없앴다. 알약은 보이는 모양일 뿐이라 라벨과 `: `도 메모장처럼 커서·선택·드래그된다.
- 커서를 라벨 안에 두고 글자를 치면 라벨이 고쳐지고, 알약 배치는 라벨마다 다시 잰다(3.3).
- 본문 시작에서 Backspace를 누르면 공백이 지워져 알약이 풀리는 동작은 그대로다.

### 테스트 — `PillCursorTest`

| 테스트 | 확인하는 것 |
|---|---|
| `selectionInsidePill_staysWhereItIsPut` | 라벨 시작 ~ 본문 시작 사이 모든 위치에 커서를 두거나, 거기서 본문 끝까지 선택해도 그대로 남는다 |
| `tapInsideLabel_putsCursorThere` | 실제 탭 제스처로 라벨 가운데 글자 사이를 누르면 커서가 그 자리에 온다 |

- 두 테스트 모두 예전 규칙으로 돌리면 실패하는 것을 확인했다(커서 2를 두면 8로 이동).
- 테스트용 선택 액션(`SetSelection`)은 역방향 선택을 정방향으로 바꿔 넣는다. 그래서 선택은 방향을 빼고 범위만 비교한다.
- 길게 눌러 끄는 선택은 Android 기본대로 단어 단위로 넓어진다. 라벨 앞에 붙는 게 정상이라 순간이동과 구분되지 않는다. 그래서 제스처 테스트는 탭으로 한다.

---

## 11. 문단 오른쪽을 누르면 다음 문단으로 가던 버그 (`154bcd7`)

### 증상

[2]에서 5번째 문단의 글자 오른쪽 빈 곳을 누르면 커서가 6번째 문단 맨 앞에 놓였다. [0]에서는 5번째 문단 끝에 놓인다.

### 원인

- 화면용 텍스트는 문단마다 `ParagraphStyle`을 건다. Compose(`MultiParagraphIntrinsics`)는 스타일 범위마다 Android 레이아웃을 하나씩 따로 만든다.
- 문단을 끝내는 `
`은 공백(`PARAGRAPH_BREAK`)으로 바뀌어 그 문단의 마지막 글자가 된다.
- 누른 위치를 offset으로 바꾸는 `Layout.getOffsetForHorizontal`은 레이아웃의 **마지막 줄**에서는 줄 끝 글자 뒤까지를 후보로 본다. 줄 오른쪽 빈 곳을 누르면 그 공백 뒤, 곧 다음 문단 맨 앞이 나온다.
- 마지막 줄이 아니면 줄 끝 글자 앞까지만 본다. 텍스트 전체가 레이아웃 하나인 [0]는 그래서 문제가 없다.
- [1]도 같은 구조라 같은 버그가 있을 것으로 보인다(확인하지 않음).

### 해결 — `ParagraphEndTapFix`

- 텍스트 영역 Box에 `pointerInput`을 걸어, 누르는 동안의 손가락 위치를 기억한다.
    - `Initial` 단계에서 적는다. 텍스트 필드가 선택을 바꾸는 `Main` 단계보다 먼저다.
    - 이벤트를 소비하지 않아 탭·드래그 처리는 그대로다.
    - `Final` 단계에서 손가락이 모두 떨어졌으면 지운다. 같은 이벤트의 `Main` 단계 선택 변경까지는 위치가 남는다.
- `inputTransformation`에서, 누르는 동안 바뀐 선택 끝이 문단 맨 앞이고 손가락이 그 앞 문단의 마지막 줄에 있으면 한 칸 앞(앞 문단 끝)으로 되돌린다.
- 한계: 선택 손잡이는 별도 창(Popup)에서 끌려 손가락 위치가 오지 않는다. 손잡이를 줄 오른쪽 끝으로 끄는 경우는 고치지 못한다.

### 테스트 — `ParagraphTapTest`

| 테스트 | 확인하는 것 |
|---|---|
| `tapRightOfParagraph_putsCursorAtItsEnd` | 문단마다 필드 오른쪽 끝 근처를 실제로 탭하면 커서가 그 문단 끝에 온다 |
| `tapLeftOfParagraph_putsCursorAtItsStart` | 문단마다 왼쪽 끝(불릿·알약 자리)을 탭하면 커서가 그 문단 맨 앞에 온다. 고친 뒤에도 반대쪽으로 잘못 끌려가지 않는지 본다 |

- 문단은 일반 문단과 알약 문단을 섞는다.
- 보정을 연결하기 전 코드로 돌리면 실패한다(`expected TextRange(6) but was TextRange(7)`).

---

## 12. 실기기로 확인할 때 쓴 방법

다음에 비슷한 문제를 볼 때 참고한다.

- **좌표 실측**: `drawPills` 안에 임시로 `Log.d`를 넣어 `getHorizontalPosition`·`getBoundingBox` 값을 찍고, `adb logcat -s <태그>`로 본다. 커밋 전에 지운다.
- **화면 조작**: `adb shell input tap x y`, `adb shell input text "abc:%s"`(`%s`는 공백), `adb shell input keyevent KEYCODE_ENTER`.
- **스크린샷**: `adb exec-out screencap -p > 파일.png`. 필요한 부분만 잘라 본다.
- **Compose 소스 확인**: `~/.gradle/caches/modules-2/files-2.1/androidx.compose.*/<모듈>/<버전>/*/*-sources.jar`를 `unzip -p`로 읽는다. 이번에 확인한 것:
    - `foundation` `TextFieldSelectionState.copy()/cut()` — 화면용 텍스트 사용, 지운 뒤 클립보드에 씀
    - `ui-text` `SpannableExtensions.setTextIndent` — 들여쓰기 `ceil` 정수화
    - `ui` `Clipboard`, `AndroidClipboard` 인터페이스
