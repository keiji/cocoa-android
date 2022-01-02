package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.keiji.cocoa.android.exposure_notification.AppDatabase
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.Infectiousness
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ReportType
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureDataBaseModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureInformationModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowAndScanInstancesModel
import dev.keiji.cocoa.android.exposure_notification.model.ScanInstanceModel
import dev.keiji.cocoa.android.exposure_notification.model.State
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ExposureDataDaoTest {

    private lateinit var db: AppDatabase

    private lateinit var exposureDataDao: ExposureDataDao

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        exposureDataDao = db.exposureDataDao()
    }

    @After
    @Throws(IOException::class)
    fun disposeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectTest(): Unit = runBlocking {
        val dateMillisSinceEpoch = DateTime.now(DateTimeZone.UTC).millis

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = State.Downloaded.ordinal,
            isListed = true,
        )

        val dailySummaryModel = DailySummaryModel(
            id = 0,
            exposureDataId = 0,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            summaryData = null,
            confirmedClinicalDiagnosisSummary = null,
            confirmedTestSummary = null,
            recursiveSummary = null,
            selfReportedSummary = null,
        )

        val exposureWindowModel = ExposureWindowModel(
            id = 0,
            exposureDataId = 0,
            calibrationConfidence = 2,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            infectiousness = Infectiousness.STANDARD.ordinal,
            reportType = ReportType.CONFIRMED_TEST.ordinal,
        )
        val scanInstanceList = listOf(
            ScanInstanceModel(
                id = 0,
                exposureWindowId = 0,
                minAttenuationDb = 10,
                secondsSinceLastScan = 20,
                typicalAttenuationDb = 30,
            ),
            ScanInstanceModel(
                id = 0,
                exposureWindowId = 0,
                minAttenuationDb = 11,
                secondsSinceLastScan = 21,
                typicalAttenuationDb = 31,
            ),
        )
        val exposureWindowAndScanInstancesModel = ExposureWindowAndScanInstancesModel(
            exposureWindowModel = exposureWindowModel,
            scanInstances = scanInstanceList
        )

        val exposureSummaryModel = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> 20 * (index + 1) /* dummy values */ },
            daysSinceLastExposure = 2,
            matchedKeyCount = 4,
            maximumRiskScore = 100,
            summationRiskScore = 10,
        )
        val exposureInformation = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index + 1 /* dummy values */ },
            attenuationValue = 20,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            durationInMillis = 100.0,
            totalRiskScore = 10,
            transmissionRiskLevel = 4,
        )

        val exposureDataBaseModel = ExposureDataBaseModel(
            id = 0,
            region = "rEgion",
            subregion = "subregGion",
            enVersion = "verSion",
            startEpoch = 123456,
            finishEpoch = 456780,
        )

        exposureDataDao.insert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = listOf(diagnosisKeysFileModel),
            exposureSummary = exposureSummaryModel,
            exposureInformationList = listOf(exposureInformation),
            dailySummaryList = listOf(dailySummaryModel),
            exposureWindowList = listOf(exposureWindowAndScanInstancesModel),
        )

        val allModel = exposureDataDao.getAll()
        Assert.assertEquals(1, allModel.size)

        allModel[0].also { exposureDataAndAllModel ->
            Assert.assertNotNull(exposureDataAndAllModel.exposureBaseData)
            exposureDataAndAllModel.exposureBaseData.also { exposureBaseData ->
                Assert.assertTrue(exposureBaseData.id > 0)
                Assert.assertEquals("rEgion", exposureBaseData.region)
                Assert.assertEquals("subregGion", exposureBaseData.subregion)
                Assert.assertEquals("android", exposureBaseData.platform)
                Assert.assertEquals("verSion", exposureBaseData.enVersion)
                Assert.assertEquals(123456, exposureBaseData.startEpoch)
                Assert.assertEquals(456780, exposureBaseData.finishEpoch)
            }

            Assert.assertNotNull(exposureDataAndAllModel.diagnosisKeysFileList)
            Assert.assertEquals(1, exposureDataAndAllModel.diagnosisKeysFileList.size)
            exposureDataAndAllModel.diagnosisKeysFileList[0].also { diagnosisKeysFile ->
                Assert.assertTrue(diagnosisKeysFile.id > 0)
                Assert.assertTrue(diagnosisKeysFile.exposureDataId > 0)
                Assert.assertEquals("Region", diagnosisKeysFile.region)
                Assert.assertEquals("suBregion", diagnosisKeysFile.subregion)
                Assert.assertEquals("https://example.com/1234567890", diagnosisKeysFile.url)
                Assert.assertEquals(121212, diagnosisKeysFile.created)
                Assert.assertEquals(State.Downloaded.value, diagnosisKeysFile.state)
                Assert.assertEquals(true, diagnosisKeysFile.isListed)
            }

            Assert.assertNotNull(exposureDataAndAllModel.exposureSummary)
            Assert.assertNotNull(exposureDataAndAllModel.exposureInformationList)
            Assert.assertEquals(1, exposureDataAndAllModel.exposureInformationList.size)

            Assert.assertNotNull(exposureDataAndAllModel.dailySummaryList)
            Assert.assertEquals(1, exposureDataAndAllModel.dailySummaryList.size)

            Assert.assertNotNull(exposureDataAndAllModel.exposureWindowList)
            Assert.assertEquals(1, exposureDataAndAllModel.exposureWindowList.size)
        }
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectWithNullTest1(): Unit = runBlocking {
        val dateMillisSinceEpoch = DateTime.now(DateTimeZone.UTC).millis

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = State.Downloaded.ordinal,
            isListed = true,
        )

        val dailySummaryModel = DailySummaryModel(
            id = 0,
            exposureDataId = 0,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            summaryData = null,
            confirmedClinicalDiagnosisSummary = null,
            confirmedTestSummary = null,
            recursiveSummary = null,
            selfReportedSummary = null,
        )

        val exposureWindowModel = ExposureWindowModel(
            id = 0,
            exposureDataId = 0,
            calibrationConfidence = 2,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            infectiousness = Infectiousness.STANDARD.ordinal,
            reportType = ReportType.CONFIRMED_TEST.ordinal,
        )
        val scanInstanceList = listOf(
            ScanInstanceModel(
                id = 0,
                exposureWindowId = 0,
                minAttenuationDb = 10,
                secondsSinceLastScan = 20,
                typicalAttenuationDb = 30,
            ),
            ScanInstanceModel(
                id = 0,
                exposureWindowId = 0,
                minAttenuationDb = 11,
                secondsSinceLastScan = 21,
                typicalAttenuationDb = 31,
            ),
        )
        val exposureWindowAndScanInstancesModel = ExposureWindowAndScanInstancesModel(
            exposureWindowModel = exposureWindowModel,
            scanInstances = scanInstanceList
        )

        val exposureDataBaseModel = ExposureDataBaseModel(
            id = 0,
            region = "rEgion",
            subregion = "subregGion",
            enVersion = "verSion",
            startEpoch = 123456,
            finishEpoch = 456780,
        )

        exposureDataDao.insert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = listOf(diagnosisKeysFileModel),
            exposureSummary = null,
            exposureInformationList = emptyList(),
            dailySummaryList = listOf(dailySummaryModel),
            exposureWindowList = listOf(exposureWindowAndScanInstancesModel),
        )

        val allModel = exposureDataDao.getAll()
        Assert.assertEquals(1, allModel.size)

        allModel[0].also { exposureDataAndAllModel ->
            Assert.assertNotNull(exposureDataAndAllModel.diagnosisKeysFileList)

            Assert.assertNull(exposureDataAndAllModel.exposureSummary)
            Assert.assertNotNull(exposureDataAndAllModel.exposureInformationList)
            Assert.assertEquals(0, exposureDataAndAllModel.exposureInformationList.size)

            Assert.assertNotNull(exposureDataAndAllModel.dailySummaryList)
            Assert.assertEquals(1, exposureDataAndAllModel.dailySummaryList.size)

            Assert.assertNotNull(exposureDataAndAllModel.exposureWindowList)
            Assert.assertEquals(1, exposureDataAndAllModel.exposureWindowList.size)
            exposureDataAndAllModel.exposureWindowList.map { exposureWindowModelAndScanInstances ->
                Assert.assertNotNull(exposureWindowModelAndScanInstances.scanInstances)
                Assert.assertEquals(2, exposureWindowModelAndScanInstances.scanInstances.size)
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectWithNullTest2(): Unit = runBlocking {
        val dateMillisSinceEpoch = DateTime.now(DateTimeZone.UTC).millis

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = State.Downloaded.ordinal,
            isListed = true,
        )

        val exposureSummaryModel = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> 20 * (index + 1) /* dummy values */ },
            daysSinceLastExposure = 2,
            matchedKeyCount = 4,
            maximumRiskScore = 100,
            summationRiskScore = 10,
        )
        val exposureInformation = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index + 1 /* dummy values */ },
            attenuationValue = 20,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            durationInMillis = 100.0,
            totalRiskScore = 10,
            transmissionRiskLevel = 4,
        )

        val exposureDataBaseModel = ExposureDataBaseModel(
            id = 0,
            region = "rEgion",
            subregion = "subregGion",
            enVersion = "verSion",
            startEpoch = 123456,
            finishEpoch = 456780,
        )

        exposureDataDao.insert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = listOf(diagnosisKeysFileModel),
            exposureSummary = exposureSummaryModel,
            exposureInformationList = listOf(exposureInformation),
            dailySummaryList = emptyList(),
            exposureWindowList = emptyList(),
        )

        val allModel = exposureDataDao.getAll()
        Assert.assertEquals(1, allModel.size)

        allModel[0].also { exposureDataAndAllModel ->
            Assert.assertNotNull(exposureDataAndAllModel.diagnosisKeysFileList)

            Assert.assertNotNull(exposureDataAndAllModel.exposureSummary)
            Assert.assertNotNull(exposureDataAndAllModel.exposureInformationList)
            Assert.assertEquals(1, exposureDataAndAllModel.exposureInformationList.size)

            Assert.assertNotNull(exposureDataAndAllModel.dailySummaryList)
            Assert.assertEquals(0, exposureDataAndAllModel.dailySummaryList.size)

            Assert.assertNotNull(exposureDataAndAllModel.exposureWindowList)
            Assert.assertEquals(0, exposureDataAndAllModel.exposureWindowList.size)
        }
    }
}
