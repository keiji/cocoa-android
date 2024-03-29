package dev.keiji.cocoa.android.exposure_notification.cappuccino.entity

import com.google.android.gms.nearby.exposurenotification.DailySummariesConfig as NativeDailySummariesConfig
import com.google.android.gms.nearby.exposurenotification.Infectiousness
import com.google.android.gms.nearby.exposurenotification.ReportType as NativeReportType
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
class DailySummariesConfigTest {
    companion object {
        private const val FILENAME = "daily_summaries_config.json"
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

        val dailySummariesConfig =
            Json.decodeFromString<ExposureConfiguration.DailySummariesConfig>(jsonText)
        Assert.assertNotNull(dailySummariesConfig)

        Assert.assertEquals(3, dailySummariesConfig.attenuationBucketThresholdDb.size)
        dailySummariesConfig.attenuationBucketThresholdDb.also { attenuationBacketThresholdDb ->
            Assert.assertEquals(50, attenuationBacketThresholdDb[0])
            Assert.assertEquals(70, attenuationBacketThresholdDb[1])
            Assert.assertEquals(90, attenuationBacketThresholdDb[2])
        }
        Assert.assertEquals(4, dailySummariesConfig.attenuationBucketWeights.size)
        dailySummariesConfig.attenuationBucketWeights.also { attenuationBucketWeights ->
            Assert.assertEquals(1.0, attenuationBucketWeights[0], DELTA)
            Assert.assertEquals(1.2, attenuationBucketWeights[1], DELTA)
            Assert.assertEquals(1.4, attenuationBucketWeights[2], DELTA)
            Assert.assertEquals(1.6, attenuationBucketWeights[3], DELTA)
        }

        Assert.assertEquals(0, dailySummariesConfig.daysSinceExposureThreshold)

        Assert.assertEquals(2, dailySummariesConfig.infectiousnessWeights.size)
        dailySummariesConfig.infectiousnessWeights.also { infectiousnessWeights ->
            Assert.assertEquals(1.0, infectiousnessWeights["High"])
            Assert.assertEquals(1.1, infectiousnessWeights["Standard"])
            Assert.assertNull(infectiousnessWeights["None"])

        }

        Assert.assertEquals(11.0, dailySummariesConfig.minimumWindowScore, DELTA)

        Assert.assertEquals(4, dailySummariesConfig.reportTypeWeights.size)
        dailySummariesConfig.reportTypeWeights.also { reportTypeWeights ->
            Assert.assertEquals(1.0, reportTypeWeights["ConfirmedClinicalDiagnosis"])
            Assert.assertEquals(1.1, reportTypeWeights["ConfirmedTest"])
            Assert.assertEquals(1.2, reportTypeWeights["SelfReport"])
            Assert.assertEquals(1.3, reportTypeWeights["Recursive"])
        }
    }

    @Test
    fun toNativeTest() {
        val jsonText =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }

        val chinoDailySummariesConfig =
            Json.decodeFromString<ExposureConfiguration.DailySummariesConfig>(jsonText)
        Assert.assertNotNull(chinoDailySummariesConfig)

        val dailySummariesConfig: NativeDailySummariesConfig = chinoDailySummariesConfig.toNative()
        Assert.assertNotNull(dailySummariesConfig)

        Assert.assertEquals(3, dailySummariesConfig.attenuationBucketThresholdDb.size)
        dailySummariesConfig.attenuationBucketThresholdDb.also { attenuationBacketThresholdDb ->
            Assert.assertEquals(50, attenuationBacketThresholdDb[0])
            Assert.assertEquals(70, attenuationBacketThresholdDb[1])
            Assert.assertEquals(90, attenuationBacketThresholdDb[2])
        }
        Assert.assertEquals(4, dailySummariesConfig.attenuationBucketWeights.size)
        dailySummariesConfig.attenuationBucketWeights.also { attenuationBucketWeights ->
            Assert.assertEquals(1.0, attenuationBucketWeights[0], DELTA)
            Assert.assertEquals(1.2, attenuationBucketWeights[1], DELTA)
            Assert.assertEquals(1.4, attenuationBucketWeights[2], DELTA)
            Assert.assertEquals(1.6, attenuationBucketWeights[3], DELTA)
        }

        Assert.assertEquals(0, dailySummariesConfig.daysSinceExposureThreshold)

        Assert.assertEquals(2, dailySummariesConfig.infectiousnessWeights.size)
        dailySummariesConfig.infectiousnessWeights.also { infectiousnessWeights ->
            Assert.assertEquals(1.0, infectiousnessWeights[Infectiousness.HIGH])
            Assert.assertEquals(1.1, infectiousnessWeights[Infectiousness.STANDARD])
            Assert.assertNull(infectiousnessWeights[Infectiousness.NONE])

        }

        Assert.assertEquals(11.0, dailySummariesConfig.minimumWindowScore, DELTA)

        Assert.assertEquals(4, dailySummariesConfig.reportTypeWeights.size)
        dailySummariesConfig.reportTypeWeights.also { reportTypeWeights ->
            Assert.assertEquals(
                1.0,
                reportTypeWeights[NativeReportType.CONFIRMED_CLINICAL_DIAGNOSIS]
            )
            Assert.assertEquals(1.1, reportTypeWeights[NativeReportType.CONFIRMED_TEST])
            Assert.assertEquals(1.2, reportTypeWeights[NativeReportType.SELF_REPORT])
            Assert.assertEquals(1.3, reportTypeWeights[NativeReportType.RECURSIVE])
        }
    }

    @Test
    fun equalsTest1() {
        val jsonText =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }

        val chinoDailySummariesConfig1 =
            Json.decodeFromString<ExposureConfiguration.DailySummariesConfig>(jsonText)
        val chinoDailySummariesConfig2 =
            Json.decodeFromString<ExposureConfiguration.DailySummariesConfig>(jsonText)

        Assert.assertEquals(chinoDailySummariesConfig1, chinoDailySummariesConfig2)
    }
}
