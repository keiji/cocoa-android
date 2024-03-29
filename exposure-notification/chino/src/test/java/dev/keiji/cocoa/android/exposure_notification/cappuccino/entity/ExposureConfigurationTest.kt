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
class ExposureConfigurationTest {
    companion object {
        private const val FILENAME = "exposure_configuration.json"
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

        val exposureConfiguration =
            Json.decodeFromString<ExposureConfiguration>(jsonText)
        Assert.assertNotNull(exposureConfiguration)

        Assert.assertNotNull(exposureConfiguration.dailySummaryConfig)
        Assert.assertNotNull(exposureConfiguration.diagnosisKeysDataMappingConfig)
    }

    @Test
    fun equalsTest1() {
        val jsonText =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }

        val exposureConfiguration1 =
            Json.decodeFromString<ExposureConfiguration>(jsonText)
        val exposureConfiguration2 =
            Json.decodeFromString<ExposureConfiguration>(jsonText)

        Assert.assertTrue(exposureConfiguration1.equals(exposureConfiguration2))
    }
}
