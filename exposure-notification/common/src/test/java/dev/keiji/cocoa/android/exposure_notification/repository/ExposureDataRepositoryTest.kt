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
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowAndScanInstancesModel
import dev.keiji.cocoa.android.exposure_notification.model.State
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
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
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

    private lateinit var exposureSummary: ExposureSummary
    private lateinit var exposureInformationList: List<ExposureInformation>
    private lateinit var dailySummaryList: List<DailySummary>
    private lateinit var exposureWindowList: List<ExposureWindow>

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

        exposureSummary = loadObject(FILENAME_EXPOSURE_SUMMARY)
        exposureInformationList = loadObject(FILENAME_EXPOSURE_INFORMATIONS)
        dailySummaryList = loadObject(FILENAME_DAILY_SUMMARIES)
        exposureWindowList = loadObject(FILENAME_EXPOSURE_WINDOWS)

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
    fun saveTest1(): Unit = runBlocking {
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
            }
        val mockDailySummaryDao =
            mock<DailySummaryDao> {
            }
        val mockExposureWindowDao =
            mock<ExposureWindowDao> {
            }

        val repository = ExposureDataRepositoryImpl(
            mockPathSource,
            mockDateTimeSource,
            mockExposureDataDao,
            mockExposureInformationDao,
            mockDailySummaryDao,
            mockExposureWindowDao,
        )

        val exposureDataBaseModel = ExposureDataBaseModel(
            0, "reGiOn", null, "vERsion", 123232, 321231
        )

        val diagnosisKeysFileList = listOf(
            DiagnosisKeysFileModel(
                0,
                0,
                "regioN",
                "sUbRegion",
                "https://example.com/a/bb/c/",
                122333,
                State.None.value,
                true
            )
        )

        repository.save(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = diagnosisKeysFileList,
            exposureSummary = exposureSummary,
            exposureInformationList = exposureInformationList,
            dailySummaryList = dailySummaryList,
            exposureWindowList = exposureWindowList,
        )

        verify(mockExposureDataDao, times(1)).insert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = diagnosisKeysFileList,
            exposureSummary = exposureSummaryModel,
            exposureInformationList = exposureInformationModelList,
            dailySummaryList = dailySummaryModelList,
            exposureWindowList = exposureWindowAndScanInstancesList,
        )
    }

    @Test
    fun findGroupedDailySummaryListByTest(): Unit = runBlocking {
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

        val groupedDailySummary = repository.findGroupedDailySummaryListBy(dummyNow)
        Assert.assertNotNull(groupedDailySummary)
        Assert.assertEquals(8, groupedDailySummary.keys.size)

        println(groupedDailySummary.keys)

        groupedDailySummary.keys.forEach { dateMillisSinceEpoch ->
            when (dateMillisSinceEpoch) {
                1630108800000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1630108800000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1629072000000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629072000000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1630195200000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1630195200000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1629158400000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629158400000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1630281600000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1630281600000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1629244800000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629244800000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1629331200000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629331200000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1628985600000 -> {
                    val dailySummaries = groupedDailySummary[dateMillisSinceEpoch]
                    Assert.assertNotNull(dailySummaries)

                    Assert.assertEquals(1, dailySummaries!!.size)
                    dailySummaries.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1628985600000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                else -> Assert.fail()
            }
        }
    }

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

        println(groupedExposureWindow.keys)

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

    @Test
    fun findGroupedExposureInformationListByTest(): Unit = runBlocking {
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

        val groupedExposureInformation = repository.findGroupedExposureInformationListBy(dummyNow)
        Assert.assertNotNull(groupedExposureInformation)
        Assert.assertEquals(7, groupedExposureInformation.keys.size)

        println(groupedExposureInformation.keys)

        groupedExposureInformation.keys.forEach { dateMillisSinceEpoch ->
            when (dateMillisSinceEpoch) {
                1629331200000 -> {
                    val exposureInformations = groupedExposureInformation[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureInformations)

                    Assert.assertEquals(1, exposureInformations!!.size)
                    exposureInformations.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629331200000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1630281600000 -> {
                    val exposureInformations = groupedExposureInformation[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureInformations)

                    Assert.assertEquals(5, exposureInformations!!.size)
                    exposureInformations.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1630281600000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1629244800000 -> {
                    val exposureInformations = groupedExposureInformation[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureInformations)

                    Assert.assertEquals(1, exposureInformations!!.size)
                    exposureInformations.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629244800000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1629158400000 -> {
                    val exposureInformations = groupedExposureInformation[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureInformations)

                    Assert.assertEquals(1, exposureInformations!!.size)
                    exposureInformations.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629158400000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1629072000000 -> {
                    val exposureInformations = groupedExposureInformation[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureInformations)

                    Assert.assertEquals(1, exposureInformations!!.size)
                    exposureInformations.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1629072000000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1630195200000 -> {
                    val exposureInformations = groupedExposureInformation[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureInformations)

                    Assert.assertEquals(1, exposureInformations!!.size)
                    exposureInformations.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1630195200000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                1630108800000 -> {
                    val exposureInformations = groupedExposureInformation[dateMillisSinceEpoch]
                    Assert.assertNotNull(exposureInformations)

                    Assert.assertEquals(1, exposureInformations!!.size)
                    exposureInformations.forEach { exposureInformation ->
                        Assert.assertEquals(
                            1630108800000,
                            exposureInformation.dateMillisSinceEpoch
                        )
                        println(exposureInformation.dateTime.toRFC3339Format())
                    }
                }
                else -> Assert.fail()
            }
        }
    }
}
