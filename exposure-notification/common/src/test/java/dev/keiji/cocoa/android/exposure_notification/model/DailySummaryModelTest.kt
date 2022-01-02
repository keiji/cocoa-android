package dev.keiji.cocoa.android.exposure_notification.model

import org.junit.Assert
import org.junit.Test

class DailySummaryModelTest {

    private fun generateModel() = DailySummaryModel(
        id = 0,
        exposureDataId = 0,
        dateMillisSinceEpoch = 12345,
        summaryData = generateSummaryDataModel(),
        confirmedClinicalDiagnosisSummary = generateSummaryDataModel(),
        confirmedTestSummary = generateSummaryDataModel(),
        recursiveSummary = generateSummaryDataModel(),
        selfReportedSummary = generateSummaryDataModel(),
    )

    private fun generateSummaryDataModel() = ExposureSummaryDataModel(
        maximumScore = 1213.09,
        scoreSum = 2342.0,
        weightedDurationSum = 423.34
    )

    @Test
    fun equalsTest1() {
        val model1 = generateModel()
        val model2 = generateModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest2() {
        val model1 = generateModel().also { it.id = 1 }
        val model2 = generateModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest3() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = generateModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest4() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = DailySummaryModel(
            id = 1,
            exposureDataId = 0,
            dateMillisSinceEpoch = 123450,
            summaryData = generateSummaryDataModel(),
            confirmedClinicalDiagnosisSummary = generateSummaryDataModel(),
            confirmedTestSummary = generateSummaryDataModel(),
            recursiveSummary = generateSummaryDataModel(),
            selfReportedSummary = generateSummaryDataModel(),
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest5() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = DailySummaryModel(
            id = 1,
            exposureDataId = 0,
            dateMillisSinceEpoch = 12345,
            summaryData = null,
            confirmedClinicalDiagnosisSummary = generateSummaryDataModel(),
            confirmedTestSummary = generateSummaryDataModel(),
            recursiveSummary = generateSummaryDataModel(),
            selfReportedSummary = generateSummaryDataModel(),
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest6() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = DailySummaryModel(
            id = 1,
            exposureDataId = 0,
            dateMillisSinceEpoch = 12345,
            summaryData = generateSummaryDataModel(),
            confirmedClinicalDiagnosisSummary = null,
            confirmedTestSummary = generateSummaryDataModel(),
            recursiveSummary = generateSummaryDataModel(),
            selfReportedSummary = generateSummaryDataModel(),
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest7() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = DailySummaryModel(
            id = 1,
            exposureDataId = 0,
            dateMillisSinceEpoch = 12345,
            summaryData = generateSummaryDataModel(),
            confirmedClinicalDiagnosisSummary = generateSummaryDataModel(),
            confirmedTestSummary = null,
            recursiveSummary = generateSummaryDataModel(),
            selfReportedSummary = generateSummaryDataModel(),
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest8() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = DailySummaryModel(
            id = 1,
            exposureDataId = 0,
            dateMillisSinceEpoch = 12345,
            summaryData = generateSummaryDataModel(),
            confirmedClinicalDiagnosisSummary = generateSummaryDataModel(),
            confirmedTestSummary = generateSummaryDataModel(),
            recursiveSummary = null,
            selfReportedSummary = generateSummaryDataModel(),
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest9() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = DailySummaryModel(
            id = 1,
            exposureDataId = 0,
            dateMillisSinceEpoch = 12345,
            summaryData = generateSummaryDataModel(),
            confirmedClinicalDiagnosisSummary = generateSummaryDataModel(),
            confirmedTestSummary = generateSummaryDataModel(),
            recursiveSummary = generateSummaryDataModel(),
            selfReportedSummary = null,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest10() {
        val model1 = generateModel().also { it.exposureDataId = 1 }
        val model2 = DailySummaryModel(
            id = 1,
            exposureDataId = 0,
            dateMillisSinceEpoch = 12345,
            summaryData = generateSummaryDataModel(),
            confirmedClinicalDiagnosisSummary = generateSummaryDataModel(),
            confirmedTestSummary = generateSummaryDataModel(),
            recursiveSummary = generateSummaryDataModel(),
            selfReportedSummary = ExposureSummaryDataModel(
                maximumScore = 3333.33,
                scoreSum = 2342.0,
                weightedDurationSum = 423.34
            ),
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }
}
