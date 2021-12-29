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
class ExposureWindowTest {
    companion object {
        private const val FILENAME = "exposure_windows.json"
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

        val exposureWindows = Json.decodeFromString<List<ExposureWindow>>(jsonText)
        Assert.assertEquals(72, exposureWindows.size)

//        exposureWindows.forEachIndexed { index, exposureWindow ->
//            if(exposureWindow.dateMillisSinceEpoch == 1634083200000) {
//                println(index)
//            }
//        }

        exposureWindows[0].also { exposureWindow ->
            Assert.assertEquals(1, exposureWindow.calibrationConfidence)
            Assert.assertEquals(1634169600000, exposureWindow.dateMillisSinceEpoch)
            Assert.assertEquals(2, exposureWindow.infectiousness)
            Assert.assertEquals(2, exposureWindow.reportType)

            Assert.assertEquals(9, exposureWindow.scanInstances.size)

            exposureWindow.scanInstances[0].also { scanInstance ->
                Assert.assertEquals(34, scanInstance.minAttenuationDb)
                Assert.assertEquals(240, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(36, scanInstance.typicalAttenuationDb)
            }
            exposureWindow.scanInstances[1].also { scanInstance ->
                Assert.assertEquals(33, scanInstance.minAttenuationDb)
                Assert.assertEquals(240, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(37, scanInstance.typicalAttenuationDb)
            }
            // ...
            exposureWindow.scanInstances[6].also { scanInstance ->
                Assert.assertEquals(31, scanInstance.minAttenuationDb)
                Assert.assertEquals(180, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(34, scanInstance.typicalAttenuationDb)
            }
        }
        exposureWindows[1].also { exposureWindow ->
            Assert.assertEquals(3, exposureWindow.calibrationConfidence)
            Assert.assertEquals(1634169600000, exposureWindow.dateMillisSinceEpoch)
            Assert.assertEquals(2, exposureWindow.infectiousness)
            Assert.assertEquals(2, exposureWindow.reportType)

            Assert.assertEquals(7, exposureWindow.scanInstances.size)

            exposureWindow.scanInstances[0].also { scanInstance ->
                Assert.assertEquals(59, scanInstance.minAttenuationDb)
                Assert.assertEquals(300, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(64, scanInstance.typicalAttenuationDb)
            }
            exposureWindow.scanInstances[1].also { scanInstance ->
                Assert.assertEquals(59, scanInstance.minAttenuationDb)
                Assert.assertEquals(240, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(60, scanInstance.typicalAttenuationDb)
            }
            // ...
            exposureWindow.scanInstances[6].also { scanInstance ->
                Assert.assertEquals(52, scanInstance.minAttenuationDb)
                Assert.assertEquals(240, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(55, scanInstance.typicalAttenuationDb)
            }
        }

        exposureWindows[56].also { exposureWindow ->
            Assert.assertEquals(1, exposureWindow.calibrationConfidence)
            Assert.assertEquals(1634083200000, exposureWindow.dateMillisSinceEpoch)
            Assert.assertEquals(2, exposureWindow.infectiousness)
            Assert.assertEquals(2, exposureWindow.reportType)

            Assert.assertEquals(8, exposureWindow.scanInstances.size)

            exposureWindow.scanInstances[0].also { scanInstance ->
                Assert.assertEquals(39, scanInstance.minAttenuationDb)
                Assert.assertEquals(240, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(41, scanInstance.typicalAttenuationDb)
            }
            exposureWindow.scanInstances[1].also { scanInstance ->
                Assert.assertEquals(38, scanInstance.minAttenuationDb)
                Assert.assertEquals(300, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(41, scanInstance.typicalAttenuationDb)
            }
            // ...
            exposureWindow.scanInstances[6].also { scanInstance ->
                Assert.assertEquals(37, scanInstance.minAttenuationDb)
                Assert.assertEquals(240, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(40, scanInstance.typicalAttenuationDb)
            }
        }

        val intervalInMillis =
            exposureWindows[0].dateMillisSinceEpoch - exposureWindows[56].dateMillisSinceEpoch
        Assert.assertEquals(TimeUnit.DAYS.toMillis(1), intervalInMillis)

        // ...

        exposureWindows[71].also { exposureWindow ->
            print(exposureWindow)
            Assert.assertEquals(3, exposureWindow.calibrationConfidence)
            Assert.assertEquals(1634083200000, exposureWindow.dateMillisSinceEpoch)
            Assert.assertEquals(2, exposureWindow.infectiousness)
            Assert.assertEquals(2, exposureWindow.reportType)

            Assert.assertEquals(7, exposureWindow.scanInstances.size)

            exposureWindow.scanInstances[0].also { scanInstance ->
                Assert.assertEquals(53, scanInstance.minAttenuationDb)
                Assert.assertEquals(300, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(56, scanInstance.typicalAttenuationDb)
            }
            exposureWindow.scanInstances[1].also { scanInstance ->
                Assert.assertEquals(53, scanInstance.minAttenuationDb)
                Assert.assertEquals(300, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(55, scanInstance.typicalAttenuationDb)
            }
            exposureWindow.scanInstances[6].also { scanInstance ->
                Assert.assertEquals(53, scanInstance.minAttenuationDb)
                Assert.assertEquals(240, scanInstance.secondsSinceLastScan)
                Assert.assertEquals(54, scanInstance.typicalAttenuationDb)
            }
        }
    }
}