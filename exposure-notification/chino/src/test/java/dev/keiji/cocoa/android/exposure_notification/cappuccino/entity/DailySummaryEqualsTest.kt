package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import org.junit.Assert
import org.junit.Test

class DailySummaryEqualsTest {

    private fun createDailySummary() = DailySummary(
        dateMillisSinceEpoch = 12345,
        summaryData = createExposureSummaryData(),
        confirmedClinicalDiagnosisSummary = createExposureSummaryData(),
        confirmedTestSummary = createExposureSummaryData(),
        recursiveSummary = createExposureSummaryData(),
        selfReportedSummary = createExposureSummaryData(),
    )

    private fun createExposureSummaryData() = ExposureSummaryData(
        maximumScore = 12.345,
        scoreSum = 234.0,
        weightedDurationSum = 32.1,
    )

    @Test
    fun equalsTest1() {
        val object1 = createDailySummary()
        val object2 = createDailySummary()

        Assert.assertEquals(object1.hashCode(), object2.hashCode())
        Assert.assertTrue(object1.equals(object2))
    }

    @Test
    fun equalsTest2() {
        val object1 = createDailySummary()
        val object2 = DailySummary(
            dateMillisSinceEpoch = 412345, //
            summaryData = createExposureSummaryData(),
            confirmedClinicalDiagnosisSummary = createExposureSummaryData(),
            confirmedTestSummary = createExposureSummaryData(),
            recursiveSummary = createExposureSummaryData(),
            selfReportedSummary = createExposureSummaryData(),
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest3() {
        val object1 = createDailySummary()
        val object2 = DailySummary(
            dateMillisSinceEpoch = 12345,
            summaryData = createExposureSummaryData(),
            confirmedClinicalDiagnosisSummary = createExposureSummaryData(),
            confirmedTestSummary = createExposureSummaryData(),
            recursiveSummary = createExposureSummaryData(),
            selfReportedSummary = ExposureSummaryData(
                maximumScore = 112.345, //
                scoreSum = 234.0,
                weightedDurationSum = 32.1,
            ),
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest4() {
        val object1 = createDailySummary()
        val object2 = DailySummary(
            dateMillisSinceEpoch = 12345,
            summaryData = null, //
            confirmedClinicalDiagnosisSummary = createExposureSummaryData(),
            confirmedTestSummary = createExposureSummaryData(),
            recursiveSummary = createExposureSummaryData(),
            selfReportedSummary = createExposureSummaryData(),
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest5() {
        val object1 = createDailySummary()
        val object2 = DailySummary(
            dateMillisSinceEpoch = 12345,
            summaryData = createExposureSummaryData(),
            confirmedClinicalDiagnosisSummary = null, //
            confirmedTestSummary = createExposureSummaryData(),
            recursiveSummary = createExposureSummaryData(),
            selfReportedSummary = createExposureSummaryData(),
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest6() {
        val object1 = createDailySummary()
        val object2 = DailySummary(
            dateMillisSinceEpoch = 12345,
            summaryData = createExposureSummaryData(),
            confirmedClinicalDiagnosisSummary = createExposureSummaryData(),
            confirmedTestSummary = null, //
            recursiveSummary = createExposureSummaryData(),
            selfReportedSummary = createExposureSummaryData(),
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest7() {
        val object1 = createDailySummary()
        val object2 = DailySummary(
            dateMillisSinceEpoch = 12345,
            summaryData = createExposureSummaryData(),
            confirmedClinicalDiagnosisSummary = createExposureSummaryData(),
            confirmedTestSummary = createExposureSummaryData(),
            recursiveSummary = null, //
            selfReportedSummary = createExposureSummaryData(),
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest8() {
        val object1 = createDailySummary()
        val object2 = DailySummary(
            dateMillisSinceEpoch = 12345,
            summaryData = createExposureSummaryData(),
            confirmedClinicalDiagnosisSummary = createExposureSummaryData(),
            confirmedTestSummary = createExposureSummaryData(),
            recursiveSummary = createExposureSummaryData(),
            selfReportedSummary = null, //
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }
}
