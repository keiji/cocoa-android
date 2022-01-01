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
import java.util.concurrent.TimeUnit

@RunWith(BlockJUnit4ClassRunner::class)
class ExposureInformationTest {
    companion object {
        private const val FILENAME = "exposure_informations.json"
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

        val exposureInformations = Json.decodeFromString<List<ExposureInformation>>(jsonText)
        Assert.assertEquals(11, exposureInformations.size)

        exposureInformations[0].also { exposureInformation ->
            Assert.assertEquals(4, exposureInformation.attenuationDurationsInMillis.size)
            exposureInformation.attenuationDurationsInMillis.also { attenuationDurationsInMillis ->
                Assert.assertEquals(1800000, attenuationDurationsInMillis[0])
                Assert.assertEquals(0, attenuationDurationsInMillis[1])
                Assert.assertEquals(0, attenuationDurationsInMillis[2])
                Assert.assertEquals(0, attenuationDurationsInMillis[3])
            }
            Assert.assertEquals(4, exposureInformation.attenuationDurationsInMillis.size)
            Assert.assertEquals(1629331200000, exposureInformation.dateMillisSinceEpoch)
            Assert.assertEquals(1800000.0, exposureInformation.durationInMillis, DELTA)
            Assert.assertEquals(255, exposureInformation.totalRiskScore)
            Assert.assertEquals(4, exposureInformation.transmissionRiskLevel)
        }
        exposureInformations[1].also { exposureInformation ->
            Assert.assertEquals(4, exposureInformation.attenuationDurationsInMillis.size)
            exposureInformation.attenuationDurationsInMillis.also { attenuationDurationsInMillis ->
                Assert.assertEquals(1800000, attenuationDurationsInMillis[0])
                Assert.assertEquals(0, attenuationDurationsInMillis[1])
                Assert.assertEquals(0, attenuationDurationsInMillis[2])
                Assert.assertEquals(0, attenuationDurationsInMillis[3])

            }
            Assert.assertEquals(4, exposureInformation.attenuationDurationsInMillis.size)
            Assert.assertEquals(1630281600000, exposureInformation.dateMillisSinceEpoch)
            Assert.assertEquals(1800000.0, exposureInformation.durationInMillis, DELTA)
            Assert.assertEquals(255, exposureInformation.totalRiskScore)
            Assert.assertEquals(4, exposureInformation.transmissionRiskLevel)
        }

        // ...

        exposureInformations[10].also { exposureInformation ->
            Assert.assertEquals(4, exposureInformation.attenuationDurationsInMillis.size)
            exposureInformation.attenuationDurationsInMillis.also { attenuationDurationsInMillis ->
                Assert.assertEquals(780000, attenuationDurationsInMillis[0])
                Assert.assertEquals(480000, attenuationDurationsInMillis[1])
                Assert.assertEquals(0, attenuationDurationsInMillis[2])
                Assert.assertEquals(0, attenuationDurationsInMillis[3])

            }
            Assert.assertEquals(4, exposureInformation.attenuationDurationsInMillis.size)
            Assert.assertEquals(1630281600000, exposureInformation.dateMillisSinceEpoch)
            Assert.assertEquals(1260000.0, exposureInformation.durationInMillis, DELTA)
            Assert.assertEquals(255, exposureInformation.totalRiskScore)
            Assert.assertEquals(4, exposureInformation.transmissionRiskLevel)
        }

    }
}
