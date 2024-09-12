package com.example.objectorienteduilayer

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import java.text.DecimalFormat

object ConversionFormula {

    fun sizeToOtherSize(
        sourceCountry: CountryInfo, sourceSize: Double, targetCountry: CountryInfo
    ): Double {
        if (sourceCountry == targetCountry) {
            return sourceSize
        } else {
            // (1) 값 변환을 위한 스플라인 함수를 아래 쪽 코드에서 정의한 Map에서 꺼내옴
            val spline = splineMap[Pair(sourceCountry, targetCountry)]!!

            // (2) sourceSize가 스플라인 함수가 지원하는 input 범위 내에 있는 지 검증
            val minSize = spline.knots.first()
            val maxSize = spline.knots.last()
            if (sourceSize !in minSize..maxSize) {
                throw SizeOutOfBoundsException(
                    minSize,
                    maxSize,
                    "($sourceCountry to $targetCountry) 입력값이 (${minSize}..${maxSize}) 이내여야 합니다 (스플라인 함수의 범위를 벗어남)."
                )
            }

            // (3) 검증을 통과한 sourceSize를 변환과 동시에 return
            return spline.value(sourceSize)
        }
    }

    // sizeToOtherSize()에서 발생시킬 사용자 정의 에러
    class SizeOutOfBoundsException(
        val minSize: Double,
        val maxSize: Double,
        message: String // val이 아닌 것 같지만 val임. message는 IllegalArgumentException에서 이미 정의된 프로퍼티임. 즉, 부모의 생성자에 있던 val message를 그대로 가져온 것
    ) : IllegalArgumentException(message)

    // 소수점 셋째자리'에서' 반올림 및 형식을 x.xx로 고정
    fun formatToTwoDecimalPlaces(value: Double): String {
        val decimalFormat = DecimalFormat("0.00")
        return decimalFormat.format(value)
    }

    // 스플라인 함수 제작을 위한 데이터
    private val krSizesForSpline = doubleArrayOf(225.0, 230.0, 235.0, 240.0, 245.0, 250.0, 252.5, 255.0, 260.0, 265.0, 270.0, 275.0, 280.0, 282.5, 285.0, 290.0, 295.0, 300.0, 305.0, 307.5, 310.0, 315.0, 320.0)
    private val usSizesForSpline = doubleArrayOf(4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 9.5, 10.0, 10.5, 11.0, 11.5, 12.0, 12.5, 13.0, 13.5, 14.0, 14.5, 15.0)
    private val jpSizesForSpline = doubleArrayOf(22.5, 23.0, 23.5, 24.0, 24.5, 25.0, 25.25, 25.5, 26.0, 26.5, 27.0, 27.5, 28.0, 28.25, 28.5, 29.0, 29.5, 30.0, 30.5, 30.75, 31.0, 31.5, 32.0)
    private val euSizesForSpline = doubleArrayOf(36.0, 37.0, 37.5, 38.0, 39.0, 39.5, 40.0, 40.5, 41.5, 42.0, 42.5, 43.5, 44.0, 44.5, 45.0, 46.0, 46.5, 47.0, 48.0, 48.5, 49.0, 49.5, 50.5)

    // 데이터를 받아 스플라인 함수를 제작하는 함수
    private val splineInterpolator = SplineInterpolator()

    // 제작된 스플라인 함수들을 저장할 Map
    private val splineMap : Map<Pair<CountryInfo, CountryInfo>, PolynomialSplineFunction> = mapOf(
        // kr size to other sizes
        Pair(CountryInfo.KR, CountryInfo.US) to splineInterpolator.interpolate(krSizesForSpline, usSizesForSpline),
        Pair(CountryInfo.KR, CountryInfo.JP) to splineInterpolator.interpolate(krSizesForSpline, jpSizesForSpline),
        Pair(CountryInfo.KR, CountryInfo.EU) to splineInterpolator.interpolate(krSizesForSpline, euSizesForSpline),

        // us size to other sizes
        Pair(CountryInfo.US, CountryInfo.KR) to splineInterpolator.interpolate(usSizesForSpline, krSizesForSpline),
        Pair(CountryInfo.US, CountryInfo.JP) to splineInterpolator.interpolate(usSizesForSpline, jpSizesForSpline),
        Pair(CountryInfo.US, CountryInfo.EU) to splineInterpolator.interpolate(usSizesForSpline, euSizesForSpline),

        // jp size to other sizes
        Pair(CountryInfo.JP, CountryInfo.KR) to splineInterpolator.interpolate(jpSizesForSpline, krSizesForSpline),
        Pair(CountryInfo.JP, CountryInfo.US) to splineInterpolator.interpolate(jpSizesForSpline, usSizesForSpline),
        Pair(CountryInfo.JP, CountryInfo.EU) to splineInterpolator.interpolate(jpSizesForSpline, euSizesForSpline),

        // eu size to other sizes
        Pair(CountryInfo.EU, CountryInfo.KR) to splineInterpolator.interpolate(euSizesForSpline, krSizesForSpline),
        Pair(CountryInfo.EU, CountryInfo.US) to splineInterpolator.interpolate(euSizesForSpline, usSizesForSpline),
        Pair(CountryInfo.EU, CountryInfo.JP) to splineInterpolator.interpolate(euSizesForSpline, jpSizesForSpline),
    )
}