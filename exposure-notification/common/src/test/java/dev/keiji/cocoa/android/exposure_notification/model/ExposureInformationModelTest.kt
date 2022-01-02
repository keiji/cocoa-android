package dev.keiji.cocoa.android.exposure_notification.model

import org.junit.Assert
import org.junit.Test

class ExposureInformationModelTest {

    private fun generateExposureInformationModel() = ExposureInformationModel(
        id = 0,
        exposureDataId = 0,
        attenuationDurationsInMillis = IntArray(8) { index -> index /* dummy values */ },
        attenuationValue = 6,
        dateMillisSinceEpoch = 423423,
        durationInMillis = 5234.03,
        totalRiskScore = 21,
        transmissionRiskLevel = 4,
    )

    @Test
    fun equalsTest1() {
        val model1 = generateExposureInformationModel()
        val model2 = generateExposureInformationModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest2() {
        val model1 = generateExposureInformationModel().also { it.id = 1 }
        val model2 = generateExposureInformationModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest3() {
        val model1 = generateExposureInformationModel().also { it.exposureDataId = 1 }
        val model2 = generateExposureInformationModel()

        Assert.assertEquals(model1.hashCode(), model2.hashCode())
        Assert.assertTrue(model1.equals(model2))
    }

    @Test
    fun equalsTest4() {
        val model1 = generateExposureInformationModel()
        val model2 = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index + 1 },
            attenuationValue = 6,
            dateMillisSinceEpoch = 423423,
            durationInMillis = 5234.03,
            totalRiskScore = 21,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest5() {
        val model1 = generateExposureInformationModel()
        val model2 = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index },
            attenuationValue = 7,
            dateMillisSinceEpoch = 423423,
            durationInMillis = 5234.03,
            totalRiskScore = 21,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest6() {
        val model1 = generateExposureInformationModel()
        val model2 = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index },
            attenuationValue = 6,
            dateMillisSinceEpoch = 1423423,
            durationInMillis = 5234.03,
            totalRiskScore = 21,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest7() {
        val model1 = generateExposureInformationModel()
        val model2 = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index },
            attenuationValue = 6,
            dateMillisSinceEpoch = 423423,
            durationInMillis = 15234.03,
            totalRiskScore = 21,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest8() {
        val model1 = generateExposureInformationModel()
        val model2 = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index },
            attenuationValue = 6,
            dateMillisSinceEpoch = 423423,
            durationInMillis = 5234.03,
            totalRiskScore = 20,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }

    @Test
    fun equalsTest9() {
        val model1 = generateExposureInformationModel()
        val model2 = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index },
            attenuationValue = 6,
            dateMillisSinceEpoch = 423423,
            durationInMillis = 5234.03,
            totalRiskScore = 21,
            transmissionRiskLevel = 3,
        )

        Assert.assertNotEquals(model1.hashCode(), model2.hashCode())
        Assert.assertFalse(model1.equals(model2))
    }
}
