package dev.keiji.cocoa.android.exposure_notification.repository

import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.DailySummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureSummary
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureWindow
import dev.keiji.cocoa.android.exposure_notification.dao.DailySummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureDataDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureInformationDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureWindowDao
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowAndScanInstancesModel
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.toRFC3339Format
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.InputStreamReader

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class ExposureDataRepositoryTest {

    companion object {
        private const val FILENAME_EXPOSURE_SUMMARY = "exposure_summary.json"
        private const val FILENAME_EXPOSURE_INFORMATIONS = "exposure_informations.json"
        private const val FILENAME_DAILY_SUMMARIES = "daily_summaries.json"
        private const val FILENAME_EXPOSURE_WINDOWS = "exposure_windows.json"
    }

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var tmpFolder: TemporaryFolder

    inline fun <reified T> loadObject(fileName: String): T {
        val jsonText =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(fileName)).use { isr ->
                isr.readText()
            }
        return Json.decodeFromString(jsonText)
    }

    private lateinit var exposureSummaryModel: ExposureSummaryModel
    private lateinit var exposureInformationModelList: List<ExposureInformationModel>
    private lateinit var dailySummaryModelList: List<DailySummaryModel>

    private lateinit var exposureWindowAndScanInstancesList: List<ExposureWindowAndScanInstancesModel>

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)

        tmpFolder = TemporaryFolder().also {
            it.create()
        }

        val exposureSummary: ExposureSummary = loadObject(FILENAME_EXPOSURE_SUMMARY)
        val exposureInformationList: List<ExposureInformation> =
            loadObject(FILENAME_EXPOSURE_INFORMATIONS)
        val dailySummaryList: List<DailySummary> = loadObject(FILENAME_DAILY_SUMMARIES)
        val exposureWindowList: List<ExposureWindow> = loadObject(FILENAME_EXPOSURE_WINDOWS)

        exposureSummaryModel = ExposureSummaryModel(exposureSummary)
        exposureInformationModelList = exposureInformationList
            .map { model -> ExposureInformationModel(model) }
        dailySummaryModelList = dailySummaryList
            .map { model -> DailySummaryModel(model) }
        exposureWindowAndScanInstancesList = exposureWindowList
            .map { exposureWindow -> ExposureWindowAndScanInstancesModel(exposureWindow) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()

        tmpFolder.delete()
    }

    private val dummyNow = DateTime(
        2022,
        1, 2,
        3, 56, 4,
        129,
        DateTimeZone.UTC
    )

    @Test
    fun findGroupedExposureWindowListByTest(): Unit = runBlocking {
        val mockPathSource =
            mock<PathSource> {
                on { diagnosisKeysFileDir() } doReturn tmpFolder.root
            }
        val mockDateTimeSource =
            mock<DateTimeSource> {
                on { utcNow() } doReturn dummyNow
            }
        val mockExposureDataDao =
            mock<ExposureDataDao> {
            }
        val mockExposureInformationDao =
            mock<ExposureInformationDao> {
                onBlocking { findBy(any()) } doReturn exposureInformationModelList
            }
        val mockDailySummaryDao =
            mock<DailySummaryDao> {
                onBlocking { findBy(any()) } doReturn dailySummaryModelList
            }
        val mockExposureWindowDao =
            mock<ExposureWindowDao> {
                onBlocking { findBy(any()) } doReturn exposureWindowAndScanInstancesList
            }

        val repository = ExposureDataRepositoryImpl(
            mockPathSource,
            mockDateTimeSource,
            mockExposureDataDao,
            mockExposureInformationDao,
            mockDailySummaryDao,
            mockExposureWindowDao,
        )

        val groupedExposureWindow = repository.findGroupedExposureWindowListBy(dummyNow)
        Assert.assertNotNull(groupedExposureWindow)
        Assert.assertEquals(2, groupedExposureWindow.keys.size)

        groupedExposureWindow.keys.forEach { dateMillisSinceEpoch ->
            when (dateMillisSinceEpoch) {
                1634169600000 -> {
                    val exposureWindows = groupedExposureWindow[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureWindows)

                    Assert.assertEquals(56, exposureWindows!!.size)
                    exposureWindows.forEach { exposureWindow ->
                        Assert.assertEquals(
                            1634169600000,
                            exposureWindow.exposureWindowModel.dateMillisSinceEpoch
                        )
                        println(exposureWindow.exposureWindowModel.dateTime.toRFC3339Format())
                    }
                }
                1634083200000 -> {
                    val exposureWindows = groupedExposureWindow[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureWindows)

                    Assert.assertEquals(16, exposureWindows!!.size)
                    exposureWindows.forEach { exposureWindow ->
                        Assert.assertEquals(
                            1634083200000,
                            exposureWindow.exposureWindowModel.dateMillisSinceEpoch
                        )
                        println(exposureWindow.exposureWindowModel.dateTime.toRFC3339Format())
                    }
                }
                else -> Assert.fail()
            }
        }

    }
}
