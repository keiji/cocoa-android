package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import org.junit.Assert
import org.junit.Test

class ExposureConfigurationV1ConfigEqualsTest {

    @Test
    fun equalsTest1() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config()

        Assert.assertEquals(object1.hashCode(), object2.hashCode())
        Assert.assertTrue(object1.equals(object2))
    }

    @Test
    fun equalsTest2() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            attenuationScores = intArrayOf(4, 4, 4, 4, 4, 4, 4, 4)
        )

        Assert.assertEquals(object1.hashCode(), object2.hashCode())
        Assert.assertTrue(object1.equals(object2))
    }

    @Test
    fun equalsTest3() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            attenuationWeight = 0
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest4() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            daysSinceLastExposureScores = intArrayOf(0, 3, 4, 5, 6, 7, 8, 9)
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest5() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            daysSinceLastExposureWeight = 100
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest6() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            durationAtAttenuationThresholds = intArrayOf(25, 37)
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest7() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            durationScores = intArrayOf(1, 1, 1, 1, 1, 1, 1, 1)
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest8() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            durationWeight = 100
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest9() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            minimumRiskScore = 8
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest10() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            transmissionRiskScores = intArrayOf(4, 4, 4, 4, 4, 4, 4, 2)
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }

    @Test
    fun equalsTest11() {
        val object1 = ExposureConfiguration.V1Config()
        val object2 = ExposureConfiguration.V1Config(
            transmissionRiskWeight = 1
        )

        Assert.assertNotEquals(object1.hashCode(), object2.hashCode())
        Assert.assertFalse(object1.equals(object2))
    }
}
