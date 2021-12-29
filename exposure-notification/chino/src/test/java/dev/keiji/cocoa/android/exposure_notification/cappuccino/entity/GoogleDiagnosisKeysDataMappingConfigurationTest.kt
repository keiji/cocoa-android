package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

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
class GoogleDiagnosisKeysDataMappingConfigurationTest {
    companion object {
        private const val FILENAME = "google_diagnosis_keys_data_mapping_configuration.json"
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

        val diagnosisKeysDataMappingConfig =
            Json.decodeFromString<ExposureConfiguration.DiagnosisKeysDataMappingConfig>(jsonText)
        Assert.assertNotNull(diagnosisKeysDataMappingConfig)

        Assert.assertEquals(
            1,
            diagnosisKeysDataMappingConfig.infectiousnessWhenDaysSinceOnsetMissing
        )
        Assert.assertEquals(1, diagnosisKeysDataMappingConfig.reportTypeWhenMissing)
        Assert.assertEquals(29, diagnosisKeysDataMappingConfig.daysSinceOnsetToInfectiousness.size)

        diagnosisKeysDataMappingConfig.daysSinceOnsetToInfectiousness.also { daysSinceOnsetToInfectiousness ->
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-14])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-13])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-12])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-11])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-10])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-9])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-8])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-7])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[-6])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[-5])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[-4])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[-3])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[-2])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[-1])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[0])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[1])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[2])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[3])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[4])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[5])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[6])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[7])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[8])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[9])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[10])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[11])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[12])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[13])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[14])
        }
    }

    @Test
    fun toNativeTest() {
        val jsonText =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }

        val chinoDiagnosisKeysDataMappingConfig =
            Json.decodeFromString<ExposureConfiguration.DiagnosisKeysDataMappingConfig>(jsonText)
        Assert.assertNotNull(chinoDiagnosisKeysDataMappingConfig)

        val diagnosisKeysDataMappingConfig = chinoDiagnosisKeysDataMappingConfig.toNative()
        Assert.assertNotNull(chinoDiagnosisKeysDataMappingConfig)

        Assert.assertEquals(
            1,
            diagnosisKeysDataMappingConfig.infectiousnessWhenDaysSinceOnsetMissing
        )
        Assert.assertEquals(1, diagnosisKeysDataMappingConfig.reportTypeWhenMissing)
        Assert.assertEquals(29, diagnosisKeysDataMappingConfig.daysSinceOnsetToInfectiousness.size)

        diagnosisKeysDataMappingConfig.daysSinceOnsetToInfectiousness.also { daysSinceOnsetToInfectiousness ->
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-14])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-13])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-12])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-11])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-10])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-9])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-8])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[-7])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[-6])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[-5])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[-4])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[-3])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[-2])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[-1])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[0])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[1])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[2])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[3])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[4])
            Assert.assertEquals(2, daysSinceOnsetToInfectiousness[5])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[6])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[7])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[8])
            Assert.assertEquals(1, daysSinceOnsetToInfectiousness[9])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[10])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[11])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[12])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[13])
            Assert.assertEquals(0, daysSinceOnsetToInfectiousness[14])
        }
    }
}
