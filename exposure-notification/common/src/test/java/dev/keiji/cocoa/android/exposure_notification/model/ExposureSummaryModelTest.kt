package dev.keiji.cocoa.android.exposure_notification.model

import org.junit.Assert
import org.junit.Test

class ExposureSummaryModelTest {

    private fun generageDummyExposureSummaryModel() = ExposureSummaryModel(
        id = 0,
        exposureDataId = 0,
        attenuationDurationsInMillis = IntArray(3) { index -> index /* dummy values */ },
        daysSinceLastExposure = 1,
        matchedKeyCount = 11,
        maximumRiskScore = 48,
        summationRiskScore = 200,
    )

    @Test
    fun equalsTest1() {
        val model1 = generageDummyExposureSummaryModel()
        val model2 = generageDummyExposureSummaryModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest2() {
        val model1 = generageDummyExposureSummaryModel().also { it.id = 1 }
        val model2 = generageDummyExposureSummaryModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest3() {
        val model1 = generageDummyExposureSummaryModel().also { it.exposureDataId = 1 }
        val model2 = generageDummyExposureSummaryModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest4() {
        val model1 = generageDummyExposureSummaryModel()
        val model2 = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> index + 10 },
            daysSinceLastExposure = 1,
            matchedKeyCount = 11,
            maximumRiskScore = 48,
            summationRiskScore = 200,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest5() {
        val model1 = generageDummyExposureSummaryModel()
        val model2 = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> index /* dummy values */ },
            daysSinceLastExposure = 13,
            matchedKeyCount = 11,
            maximumRiskScore = 48,
            summationRiskScore = 200,
        )
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest6() {
        val model1 = generageDummyExposureSummaryModel()
        val model2 = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> index /* dummy values */ },
            daysSinceLastExposure = 1,
            matchedKeyCount = 4,
            maximumRiskScore = 48,
            summationRiskScore = 200,
        )
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest7() {
        val model1 = generageDummyExposureSummaryModel()
        val model2 = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> index /* dummy values */ },
            daysSinceLastExposure = 1,
            matchedKeyCount = 11,
            maximumRiskScore = 81,
            summationRiskScore = 200,
        )
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest8() {
        val model1 = generageDummyExposureSummaryModel()
        val model2 = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> index /* dummy values */ },
            daysSinceLastExposure = 1,
            matchedKeyCount = 11,
            maximumRiskScore = 48,
            summationRiskScore = 220,
        )
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

}
