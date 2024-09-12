package com.example.objectorienteduilayer

import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

class ConversionFormulaTest {

    private lateinit var testCases: List<Map<CountryInfo, Double>>

    @Before
    fun setUp() {
        testCases = listOf(
            mapOf(CountryInfo.KR to 225.0, CountryInfo.US to 4.0, CountryInfo.JP to 22.5, CountryInfo.EU to 36.0), // 1
            mapOf(CountryInfo.KR to 230.0, CountryInfo.US to 4.5, CountryInfo.JP to 23.0, CountryInfo.EU to 37.0), // 2
            mapOf(CountryInfo.KR to 235.0, CountryInfo.US to 5.0, CountryInfo.JP to 23.5, CountryInfo.EU to 37.5), // 3
            mapOf(CountryInfo.KR to 240.0, CountryInfo.US to 5.5, CountryInfo.JP to 24.0, CountryInfo.EU to 38.0), // 4
            mapOf(CountryInfo.KR to 245.0, CountryInfo.US to 6.0, CountryInfo.JP to 24.5, CountryInfo.EU to 39.0), // 5
            mapOf(CountryInfo.KR to 250.0, CountryInfo.US to 6.5, CountryInfo.JP to 25.0, CountryInfo.EU to 39.5), // 6
            mapOf(CountryInfo.KR to 252.5, CountryInfo.US to 7.0, CountryInfo.JP to 25.25, CountryInfo.EU to 40.0), // 7
            mapOf(CountryInfo.KR to 255.0, CountryInfo.US to 7.5, CountryInfo.JP to 25.5, CountryInfo.EU to 40.5), // 8
            mapOf(CountryInfo.KR to 260.0, CountryInfo.US to 8.0, CountryInfo.JP to 26.0, CountryInfo.EU to 41.5), // 9
            mapOf(CountryInfo.KR to 265.0, CountryInfo.US to 8.5, CountryInfo.JP to 26.5, CountryInfo.EU to 42.0), // 10
            mapOf(CountryInfo.KR to 270.0, CountryInfo.US to 9.0, CountryInfo.JP to 27.0, CountryInfo.EU to 42.5), // 11
            mapOf(CountryInfo.KR to 275.0, CountryInfo.US to 9.5, CountryInfo.JP to 27.5, CountryInfo.EU to 43.5), // 12
            mapOf(CountryInfo.KR to 280.0, CountryInfo.US to 10.0, CountryInfo.JP to 28.0, CountryInfo.EU to 44.0), // 13
            mapOf(CountryInfo.KR to 282.5, CountryInfo.US to 10.5, CountryInfo.JP to 28.25, CountryInfo.EU to 44.5), // 14
            mapOf(CountryInfo.KR to 285.0, CountryInfo.US to 11.0, CountryInfo.JP to 28.5, CountryInfo.EU to 45.0), // 15
            mapOf(CountryInfo.KR to 290.0, CountryInfo.US to 11.5, CountryInfo.JP to 29.0, CountryInfo.EU to 46.0), // 16
            mapOf(CountryInfo.KR to 295.0, CountryInfo.US to 12.0, CountryInfo.JP to 29.5, CountryInfo.EU to 46.5), // 17
            mapOf(CountryInfo.KR to 300.0, CountryInfo.US to 12.5, CountryInfo.JP to 30.0, CountryInfo.EU to 47.0), // 18
            mapOf(CountryInfo.KR to 305.0, CountryInfo.US to 13.0, CountryInfo.JP to 30.5, CountryInfo.EU to 48.0), // 19
            mapOf(CountryInfo.KR to 307.5, CountryInfo.US to 13.5, CountryInfo.JP to 30.75, CountryInfo.EU to 48.5), // 20
            mapOf(CountryInfo.KR to 310.0, CountryInfo.US to 14.0, CountryInfo.JP to 31.0, CountryInfo.EU to 49.0), // 21
            mapOf(CountryInfo.KR to 315.0, CountryInfo.US to 14.5, CountryInfo.JP to 31.5, CountryInfo.EU to 49.5), // 22
            mapOf(CountryInfo.KR to 320.0, CountryInfo.US to 15.0, CountryInfo.JP to 32.0, CountryInfo.EU to 50.5), // 23
        )
    }

    @Test
    fun sizeToOtherSize_givenTestCases_returnExpectedResult() {
        val countries = arrayOf(CountryInfo.KR, CountryInfo.US, CountryInfo.JP, CountryInfo.EU)

        for (map in testCases) {
            for (sourceCountry in countries) {
                for (targetCountry in countries) {
                    val result = ConversionFormula.sizeToOtherSize(
                        sourceCountry, map[sourceCountry]!!, targetCountry
                    )
                    Truth.assertThat(result).isEqualTo(map[targetCountry]!!)
                }
            }
        }
    }
}
