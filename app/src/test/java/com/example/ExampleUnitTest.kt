package com.example

import com.example.data.CsvManager
import com.example.model.SeriesData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testSeriesAndPlatformRelations() {
        assertEquals(8, SeriesData.SERIES_LIST.size)
        for (series in SeriesData.SERIES_LIST) {
            assertEquals("Each series must contain exactly 3 platforms", 3, series.platforms.size)
        }
        assertEquals("11-Series", SeriesData.getSeriesForPlatform("1111bet.bdt"))
        assertEquals("Bk-Series", SeriesData.getSeriesForPlatform("Bk999.com"))
    }

    @Test
    fun testPlaCsvParsingAndDuplicateDetection() {
        val rawPlaCsv = """
1111,http://www.1111bet23.com/?r=kyk4462
1333,http://www.1333bet88.com/?r=ivi8750
3333,http://www.3999bet9.com/?r=ify9780
39999,http://www.3999bet9.com/?r=ify9780
Xx999,http://www.xx9990.com/?r=xth2280
        """.trimIndent()

        val result = CsvManager.parsePlaCsv(rawPlaCsv)
        // 1111 and 1333 and 3333 are valid platforms
        assertTrue(result.successful.any { it.platform == "1111bet.bdt" })
        assertTrue(result.successful.any { it.platform == "1333bet.win" })
        assertTrue(result.successful.any { it.platform == "3333bet.ai" })

        // 39999 has duplicate URL of 3333 -> should be in review list!
        assertTrue(result.mismatchedOrDuplicate.any { it.isMismatchedOrDuplicate })

        // Xx999 does not belong to the 8 series -> should be in review list!
        val unknown = result.mismatchedOrDuplicate.find { it.platform.contains("Xx999") }
        assertNotNull(unknown)
        assertTrue(unknown!!.isMismatchedOrDuplicate)
    }
}
