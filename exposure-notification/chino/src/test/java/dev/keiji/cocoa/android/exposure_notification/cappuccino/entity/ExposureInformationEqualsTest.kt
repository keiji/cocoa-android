package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import org.junit.Assert
import org.junit.Test

class ExposureInformationEqualsTest {

    private fun createExposureInformation() = ExposureInformation(
        attenuationDurationsInMillis = IntArray(4) { index -> index * 10 /* dummy values */ },
        attenuationValue = 40,
        dateMillisSinceEpoch = 54321,
        durationInMillis = 20000.0,
        totalRiskScore = 40,
        transmissionRiskLevel = 4,
    )

    @Test
    fun equalsTest1() {
        val object1 = createExposureInformation()
        val object2 = createExposureInformation()

        Assert.assertEquals(object1.hashCode(), object2.hashCode())
        Assert.assertTrue(object1.equals(object2))
    }

    @Test
    fun equalsTest2() {
        val object1 = createExposureInformation()
        val object2 = ExposureInformation(
            attenuationDurationsInMillis = IntArray(4) { index -> index * 5 }, //
            attenuationValue = 40,
            dateMillisSinceEpoch = 54321,
            durationInMillis = 20000.0,
            totalRiskScore = 40,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest3() {
        val object1 = createExposureInformation()
        val object2 = ExposureInformation(
            attenuationDurationsInMillis = IntArray(4) { index -> index * 10 },
            attenuationValue = 140, //
            dateMillisSinceEpoch = 54321,
            durationInMillis = 20000.0,
            totalRiskScore = 40,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest4() {
        val object1 = createExposureInformation()
        val object2 = ExposureInformation(
            attenuationDurationsInMillis = IntArray(4) { index -> index * 10 },
            attenuationValue = 40,
            dateMillisSinceEpoch = 88899, //
            durationInMillis = 20000.0,
            totalRiskScore = 40,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest5() {
        val object1 = createExposureInformation()
        val object2 = ExposureInformation(
            attenuationDurationsInMillis = IntArray(4) { index -> index * 10 },
            attenuationValue = 40,
            dateMillisSinceEpoch = 54321,
            durationInMillis = 1200.0, //
            totalRiskScore = 40,
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest6() {
        val object1 = createExposureInformation()
        val object2 = ExposureInformation(
            attenuationDurationsInMillis = IntArray(4) { index -> index * 10 },
            attenuationValue = 40,
            dateMillisSinceEpoch = 54321,
            durationInMillis = 20000.0,
            totalRiskScore = 51, //
            transmissionRiskLevel = 4,
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest7() {
        val object1 = createExposureInformation()
        val object2 = ExposureInformation(
            attenuationDurationsInMillis = IntArray(4) { index -> index * 10 },
            attenuationValue = 40,
            dateMillisSinceEpoch = 54321,
            durationInMillis = 20000.0,
            totalRiskScore = 40,
            transmissionRiskLevel = 3, //
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }
}
