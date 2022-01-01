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
class ExposureSummaryTest {
    companion object {
        private const val FILENAME = "exposure_summary.json"
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

        val exposureSummary = Json.decodeFromString<ExposureSummary>(jsonText)
        Assert.assertNotNull(exposureSummary)

        Assert.assertEquals(3, exposureSummary.attenuationDurationsInMillis.size)
        exposureSummary.attenuationDurationsInMillis.also { attenuationDurationsInMinutes ->
            Assert.assertEquals(1800000, attenuationDurationsInMinutes[0])
            Assert.assertEquals(1560000, attenuationDurationsInMinutes[1])
            Assert.assertEquals(0, attenuationDurationsInMinutes[2])
        }
        Assert.assertEquals(0, exposureSummary.daysSinceLastExposure)
        Assert.assertEquals(11, exposureSummary.matchedKeyCount)
        Assert.assertEquals(255, exposureSummary.maximumRiskScore)
        Assert.assertEquals(10280, exposureSummary.summationRiskScore)
    }
}
