package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration as NativeExposureConfiguration
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
class GoogleExposureConfigurationV1Test {
    companion object {
        private const val FILENAME = "google_exposure_configuration.json"
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

        val v1Config =
            Json.decodeFromString<ExposureConfiguration.V1Config>(jsonText)
        Assert.assertNotNull(v1Config)

        Assert.assertEquals(8, v1Config.attenuationScores.size)
        v1Config.attenuationScores.also { attenuationScores ->
            Assert.assertEquals(1, attenuationScores[0])
            Assert.assertEquals(2, attenuationScores[1])
            Assert.assertEquals(3, attenuationScores[2])
            Assert.assertEquals(4, attenuationScores[3])
            Assert.assertEquals(5, attenuationScores[4])
            Assert.assertEquals(6, attenuationScores[5])
            Assert.assertEquals(7, attenuationScores[6])
            Assert.assertEquals(8, attenuationScores[7])
        }
        Assert.assertEquals(51, v1Config.attenuationWeight)
        Assert.assertEquals(8, v1Config.daysSinceLastExposureScores.size)
        v1Config.daysSinceLastExposureScores.also { daysSinceLastExposureScores ->
            Assert.assertEquals(8, daysSinceLastExposureScores[0])
            Assert.assertEquals(7, daysSinceLastExposureScores[1])
            Assert.assertEquals(6, daysSinceLastExposureScores[2])
            Assert.assertEquals(5, daysSinceLastExposureScores[3])
            Assert.assertEquals(4, daysSinceLastExposureScores[4])
            Assert.assertEquals(3, daysSinceLastExposureScores[5])
            Assert.assertEquals(2, daysSinceLastExposureScores[6])
            Assert.assertEquals(1, daysSinceLastExposureScores[7])
        }
        Assert.assertEquals(52, v1Config.daysSinceLastExposureWeight)
        Assert.assertEquals(2, v1Config.durationAtAttenuationThresholds.size)
        Assert.assertEquals(8, v1Config.durationScores.size)
        v1Config.durationScores.also { durationScores ->
            Assert.assertEquals(7, durationScores[0])
            Assert.assertEquals(6, durationScores[1])
            Assert.assertEquals(5, durationScores[2])
            Assert.assertEquals(4, durationScores[3])
            Assert.assertEquals(3, durationScores[4])
            Assert.assertEquals(2, durationScores[5])
            Assert.assertEquals(1, durationScores[6])
            Assert.assertEquals(0, durationScores[7])
        }
        Assert.assertEquals(53, v1Config.durationWeight)
        Assert.assertEquals(4, v1Config.minimumRiskScore)
        Assert.assertEquals(8, v1Config.transmissionRiskScores.size)
        v1Config.transmissionRiskScores.also { transmissionRiskScores ->
            Assert.assertEquals(0, transmissionRiskScores[0])
            Assert.assertEquals(1, transmissionRiskScores[1])
            Assert.assertEquals(2, transmissionRiskScores[2])
            Assert.assertEquals(3, transmissionRiskScores[3])
            Assert.assertEquals(4, transmissionRiskScores[4])
            Assert.assertEquals(5, transmissionRiskScores[5])
            Assert.assertEquals(6, transmissionRiskScores[6])
            Assert.assertEquals(7, transmissionRiskScores[7])
        }
        Assert.assertEquals(54, v1Config.transmissionRiskWeight)

    }

    @Test
    fun toNativeTest() {
        val jsonText =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }

        val chinoV1Config =
            Json.decodeFromString<ExposureConfiguration.V1Config>(jsonText)
        Assert.assertNotNull(chinoV1Config)

        val v1Config: NativeExposureConfiguration = chinoV1Config.toNative()
        Assert.assertNotNull(v1Config)

        Assert.assertEquals(8, v1Config.attenuationScores.size)
        v1Config.attenuationScores.also { attenuationScores ->
            Assert.assertEquals(1, attenuationScores[0])
            Assert.assertEquals(2, attenuationScores[1])
            Assert.assertEquals(3, attenuationScores[2])
            Assert.assertEquals(4, attenuationScores[3])
            Assert.assertEquals(5, attenuationScores[4])
            Assert.assertEquals(6, attenuationScores[5])
            Assert.assertEquals(7, attenuationScores[6])
            Assert.assertEquals(8, attenuationScores[7])
        }
        Assert.assertEquals(51, v1Config.attenuationWeight)
        Assert.assertEquals(8, v1Config.daysSinceLastExposureScores.size)
        v1Config.daysSinceLastExposureScores.also { daysSinceLastExposureScores ->
            Assert.assertEquals(8, daysSinceLastExposureScores[0])
            Assert.assertEquals(7, daysSinceLastExposureScores[1])
            Assert.assertEquals(6, daysSinceLastExposureScores[2])
            Assert.assertEquals(5, daysSinceLastExposureScores[3])
            Assert.assertEquals(4, daysSinceLastExposureScores[4])
            Assert.assertEquals(3, daysSinceLastExposureScores[5])
            Assert.assertEquals(2, daysSinceLastExposureScores[6])
            Assert.assertEquals(1, daysSinceLastExposureScores[7])
        }
        Assert.assertEquals(52, v1Config.daysSinceLastExposureWeight)
        Assert.assertEquals(2, v1Config.durationAtAttenuationThresholds.size)
        Assert.assertEquals(8, v1Config.durationScores.size)
        v1Config.durationScores.also { durationScores ->
            Assert.assertEquals(7, durationScores[0])
            Assert.assertEquals(6, durationScores[1])
            Assert.assertEquals(5, durationScores[2])
            Assert.assertEquals(4, durationScores[3])
            Assert.assertEquals(3, durationScores[4])
            Assert.assertEquals(2, durationScores[5])
            Assert.assertEquals(1, durationScores[6])
            Assert.assertEquals(0, durationScores[7])
        }
        Assert.assertEquals(53, v1Config.durationWeight)
        Assert.assertEquals(4, v1Config.minimumRiskScore)
        Assert.assertEquals(8, v1Config.transmissionRiskScores.size)
        v1Config.transmissionRiskScores.also { transmissionRiskScores ->
            Assert.assertEquals(0, transmissionRiskScores[0])
            Assert.assertEquals(1, transmissionRiskScores[1])
            Assert.assertEquals(2, transmissionRiskScores[2])
            Assert.assertEquals(3, transmissionRiskScores[3])
            Assert.assertEquals(4, transmissionRiskScores[4])
            Assert.assertEquals(5, transmissionRiskScores[5])
            Assert.assertEquals(6, transmissionRiskScores[6])
            Assert.assertEquals(7, transmissionRiskScores[7])
        }
        Assert.assertEquals(54, v1Config.transmissionRiskWeight)

    }
}
