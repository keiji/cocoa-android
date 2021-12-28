package dev.keiji.cocoa.android.exposure_notification.core.entity

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner
import java.io.InputStreamReader

@RunWith(BlockJUnit4ClassRunner::class)
class DailySummaryTest {
    companion object {
        private const val FILENAME = "daily_summaries.json"
        private const val DELTA = 0.0000001
    }

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun deserializeTest() {
        val jsonText =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }

        val dailySummaries = Json.decodeFromString<List<DailySummary>>(jsonText)
        Assert.assertEquals(8, dailySummaries.size)

        dailySummaries[0].also { dailySummary ->
            Assert.assertEquals(1630108800000, dailySummary.dateMillisSinceEpoch)

            Assert.assertNotNull(dailySummary.summaryData)
            Assert.assertNotNull(dailySummary.confirmedClinicalDiagnosisSummary)
            Assert.assertNull(dailySummary.confirmedTestSummary)
            Assert.assertNull(dailySummary.recursiveSummary)
            Assert.assertNull(dailySummary.selfReportedSummary)

            dailySummary.summaryData?.also { exposureSummaryData ->
                Assert.assertEquals(1860.0, exposureSummaryData.maximumScore, DELTA)
                Assert.assertEquals(27600.0, exposureSummaryData.scoreSum, DELTA)
                Assert.assertEquals(27600.0, exposureSummaryData.weightedDurationSum, DELTA)
            }

            dailySummary.confirmedClinicalDiagnosisSummary?.also { exposureSummaryData ->
                Assert.assertEquals(1860.0, exposureSummaryData.maximumScore, DELTA)
                Assert.assertEquals(27600.0, exposureSummaryData.scoreSum, DELTA)
                Assert.assertEquals(27600.0, exposureSummaryData.weightedDurationSum, DELTA)
            }
        }

        dailySummaries[1].also { dailySummary ->
            Assert.assertEquals(1629072000000, dailySummary.dateMillisSinceEpoch)

            Assert.assertNotNull(dailySummary.summaryData)
            Assert.assertNotNull(dailySummary.confirmedClinicalDiagnosisSummary)
            Assert.assertNull(dailySummary.confirmedTestSummary)
            Assert.assertNull(dailySummary.recursiveSummary)
            Assert.assertNull(dailySummary.selfReportedSummary)

            dailySummary.summaryData?.also { exposureSummaryData ->
                Assert.assertEquals(2100.0, exposureSummaryData.maximumScore, DELTA)
                Assert.assertEquals(85980.0, exposureSummaryData.scoreSum, DELTA)
                Assert.assertEquals(85980.0, exposureSummaryData.weightedDurationSum, DELTA)
            }

            dailySummary.confirmedClinicalDiagnosisSummary?.also { exposureSummaryData ->
                Assert.assertEquals(2100.0, exposureSummaryData.maximumScore, DELTA)
                Assert.assertEquals(85980.0, exposureSummaryData.scoreSum, DELTA)
                Assert.assertEquals(85980.0, exposureSummaryData.weightedDurationSum, DELTA)
            }
        }

        // ...

        dailySummaries[7].also { dailySummary ->
            Assert.assertEquals(1628985600000, dailySummary.dateMillisSinceEpoch)

            Assert.assertNotNull(dailySummary.summaryData)
            Assert.assertNull(dailySummary.confirmedClinicalDiagnosisSummary)
            Assert.assertNotNull(dailySummary.confirmedTestSummary)
            Assert.assertNull(dailySummary.recursiveSummary)
            Assert.assertNull(dailySummary.selfReportedSummary)

            dailySummary.summaryData?.also { exposureSummaryData ->
                Assert.assertEquals(2040.0, exposureSummaryData.maximumScore, DELTA)
                Assert.assertEquals(62220.0, exposureSummaryData.scoreSum, DELTA)
                Assert.assertEquals(62220.0, exposureSummaryData.weightedDurationSum, DELTA)
            }

            dailySummary.confirmedTestSummary?.also { exposureSummaryData ->
                Assert.assertEquals(2040.0, exposureSummaryData.maximumScore, DELTA)
                Assert.assertEquals(62220.0, exposureSummaryData.scoreSum, DELTA)
                Assert.assertEquals(62220.0, exposureSummaryData.weightedDurationSum, DELTA)
            }
        }
    }
}