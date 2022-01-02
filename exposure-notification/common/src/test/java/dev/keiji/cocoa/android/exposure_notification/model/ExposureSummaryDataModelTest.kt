package dev.keiji.cocoa.android.exposure_notification.model

import org.junit.Assert
import org.junit.Test

class ExposureSummaryDataModelTest {

    private fun generateSummaryDataModel() = ExposureSummaryDataModel(
        maximumScore = 1213.09,
        scoreSum = 2342.0,
        weightedDurationSum = 423.34
    )

    @Test
    fun equalsTest1() {
        val model1 = generateSummaryDataModel()
        val model2 = generateSummaryDataModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest2() {
        val model1 = generateSummaryDataModel()
        val model2 = ExposureSummaryDataModel(
            maximumScore = 5213.09,
            scoreSum = 2342.0,
            weightedDurationSum = 423.34
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest3() {
        val model1 = generateSummaryDataModel()
        val model2 = ExposureSummaryDataModel(
            maximumScore = 1213.09,
            scoreSum = 42342.0,
            weightedDurationSum = 423.34
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest4() {
        val model1 = generateSummaryDataModel()
        val model2 = ExposureSummaryDataModel(
            maximumScore = 1213.09,
            scoreSum = 22342.0,
            weightedDurationSum = 523.34
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }
}
