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
            state = DiagnosisKeysFileModel.State.Downloaded.ordinal,
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
            uniqueKey = "",
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
            subregionList = listOf("subregGion"),
            enVersion = "verSion",
            startUptime = 42342,
            startedEpoch = 123456,
            plannedEpoch = 356742,
            finishedEpoch = 456780,
            message = "hello",
        )

        exposureDataDao.upsert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = mutableListOf(diagnosisKeysFileModel),
            exposureSummary = exposureSummaryModel,
            exposureInformationList = mutableListOf(exposureInformation),
            dailySummaryList = mutableListOf(dailySummaryModel),
            exposureWindowList = mutableListOf(exposureWindowAndScanInstancesModel),
        )

        val allModel = exposureDataDao.getAll()
        Assert.assertEquals(1, allModel.size)

        allModel[0].also { exposureDataAndAllModel ->
            Assert.assertNotNull(exposureDataAndAllModel.exposureBaseData)
            exposureDataAndAllModel.exposureBaseData.also { exposureBaseData ->
                Assert.assertTrue(exposureBaseData.id > 0)
                Assert.assertEquals("rEgion", exposureBaseData.region)
                Assert.assertEquals(listOf("subregGion"), exposureBaseData.subregionList)
                Assert.assertEquals("android", exposureBaseData.platform)
                Assert.assertEquals("verSion", exposureBaseData.enVersion)
                Assert.assertEquals(42342, exposureBaseData.startUptime)
                Assert.assertEquals(356742, exposureBaseData.plannedEpoch)
                Assert.assertEquals(123456, exposureBaseData.startedEpoch)
                Assert.assertEquals(456780, exposureBaseData.finishedEpoch)
                Assert.assertEquals("hello", exposureBaseData.message)
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
                Assert.assertEquals(
                    DiagnosisKeysFileModel.State.Downloaded.value,
                    diagnosisKeysFile.state
                )
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
            state = DiagnosisKeysFileModel.State.Downloaded.ordinal,
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
            uniqueKey = "",
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
            subregionList = listOf("subregGion"),
            enVersion = "verSion",
            startUptime = 324234,
            plannedEpoch = 158340,
            startedEpoch = 123456,
            finishedEpoch = 456780,
        )

        exposureDataDao.upsert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = mutableListOf(diagnosisKeysFileModel),
            exposureSummary = null,
            exposureInformationList = mutableListOf(),
            dailySummaryList = mutableListOf(dailySummaryModel),
            exposureWindowList = mutableListOf(exposureWindowAndScanInstancesModel),
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
            state = DiagnosisKeysFileModel.State.Downloaded.ordinal,
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
            subregionList = listOf("subregGion"),
            enVersion = "verSion",
            startUptime = 42984,
            plannedEpoch = 158340,
            startedEpoch = 123456,
            finishedEpoch = 456780,
        )

        exposureDataDao.upsert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = mutableListOf(diagnosisKeysFileModel),
            exposureSummary = exposureSummaryModel,
            exposureInformationList = mutableListOf(exposureInformation),
            dailySummaryList = mutableListOf(),
            exposureWindowList = mutableListOf(),
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

    @Test
    @Throws(IOException::class)
    fun findAllTest(): Unit = runBlocking {

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = DiagnosisKeysFileModel.State.Downloaded.ordinal,
            isListed = true,
        )

        val exposureDataBaseModels = listOf(
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Finished.value,
                startUptime = 234324,
                plannedEpoch = 158340,
                startedEpoch = 223456,
                finishedEpoch = 456783,
            ),
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.ResultReceived.value,
                startUptime = 5943823,
                plannedEpoch = 158340,
                startedEpoch = 123456,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                startUptime = 242344,
                plannedEpoch = 158340,
                startedEpoch = 364543,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Finished.value,
                startUptime = 897935,
                plannedEpoch = 158340,
                startedEpoch = 123456,
                finishedEpoch = 456780,
            ),
        )

        exposureDataBaseModels.forEach { model ->
            exposureDataDao.upsert(
                exposureBaseData = model,
                diagnosisKeysFileList = mutableListOf(diagnosisKeysFileModel),
            )
        }

        val startedModels = exposureDataDao.findBy(ExposureDataBaseModel.State.Started.value)
        Assert.assertEquals(1, startedModels.size)

        startedModels[0].also { model ->
            Assert.assertEquals(364543, model.exposureBaseData.startedEpoch)
            Assert.assertTrue(model.exposureBaseData.finishedEpoch < 0)
        }

        val resultReceivedModels =
            exposureDataDao.findBy(ExposureDataBaseModel.State.ResultReceived.value)
        Assert.assertEquals(1, resultReceivedModels.size)

        resultReceivedModels[0].also { model ->
            Assert.assertEquals(123456, model.exposureBaseData.startedEpoch)
            Assert.assertTrue(model.exposureBaseData.finishedEpoch < 0)
        }

        val finishedModel = exposureDataDao.findBy(ExposureDataBaseModel.State.Finished.value)
        Assert.assertEquals(2, finishedModel.size)

        finishedModel[0].also { model ->
            Assert.assertEquals(ExposureDataBaseModel.State.Finished, model.exposureBaseData.state)
            Assert.assertEquals(223456, model.exposureBaseData.startedEpoch)
            Assert.assertEquals(456783, model.exposureBaseData.finishedEpoch)
        }
        finishedModel[1].also { model ->
            Assert.assertEquals(ExposureDataBaseModel.State.Finished, model.exposureBaseData.state)
            Assert.assertEquals(123456, model.exposureBaseData.startedEpoch)
            Assert.assertEquals(456780, model.exposureBaseData.finishedEpoch)
        }
    }

    @Test
    @Throws(IOException::class)
    fun getByTest(): Unit = runBlocking {

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = DiagnosisKeysFileModel.State.Downloaded.ordinal,
            isListed = true,
        )

        val exposureDataBaseModels = listOf(
            ExposureDataBaseModel(
                id = 0,
                priority = 10,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Finished.value,
                startUptime = 234324,
                plannedEpoch = 15834,
                startedEpoch = 223456,
                finishedEpoch = 456783,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 10,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.ResultReceived.value,
                startUptime = 5943823,
                plannedEpoch = 1583,
                startedEpoch = 123456,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                startUptime = 242344,
                plannedEpoch = 9158340,
                startedEpoch = 364543,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 10,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                startUptime = 1242344,
                plannedEpoch = 19158340,
                startedEpoch = 1364543,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 9,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Finished.value,
                startUptime = 897935,
                plannedEpoch = 758340,
                startedEpoch = 123456,
                finishedEpoch = 456780,
            ),
        )

        exposureDataBaseModels.forEach { model ->
            exposureDataDao.upsert(
                exposureBaseData = model,
                diagnosisKeysFileList = mutableListOf(diagnosisKeysFileModel),
            )
        }

        val object0 = exposureDataDao.getBy(ExposureDataBaseModel.State.Started.value)
        object0?.also { obj ->
            Assert.assertEquals(ExposureDataBaseModel.State.Started, obj.exposureBaseData.state)
            Assert.assertEquals(10, obj.exposureBaseData.priority)
            Assert.assertEquals(1242344, obj.exposureBaseData.startUptime)
            Assert.assertEquals(19158340, obj.exposureBaseData.plannedEpoch)
            Assert.assertEquals(1364543, obj.exposureBaseData.startedEpoch)
            Assert.assertEquals(-1, obj.exposureBaseData.finishedEpoch)
        }

        val object1 = exposureDataDao.getBy(ExposureDataBaseModel.State.ResultReceived.value)
        object1?.also { obj ->
            Assert.assertEquals(
                ExposureDataBaseModel.State.ResultReceived,
                obj.exposureBaseData.state
            )
            Assert.assertEquals(10, obj.exposureBaseData.priority)
            Assert.assertEquals(5943823, obj.exposureBaseData.startUptime)
            Assert.assertEquals(1583, obj.exposureBaseData.plannedEpoch)
            Assert.assertEquals(123456, obj.exposureBaseData.startedEpoch)
            Assert.assertEquals(-1, obj.exposureBaseData.finishedEpoch)
        }

        val object2 = exposureDataDao.getBy(ExposureDataBaseModel.State.Finished.value)
        object2?.also { obj ->
            Assert.assertEquals(ExposureDataBaseModel.State.Finished, obj.exposureBaseData.state)
            Assert.assertEquals(10, obj.exposureBaseData.priority)
            Assert.assertEquals(234324, obj.exposureBaseData.startUptime)
            Assert.assertEquals(15834, obj.exposureBaseData.plannedEpoch)
            Assert.assertEquals(223456, obj.exposureBaseData.startedEpoch)
            Assert.assertEquals(456783, obj.exposureBaseData.finishedEpoch)
        }

        val object3 = exposureDataDao.getBy(ExposureDataBaseModel.State.Planned.value)
        Assert.assertNull(object3)
//        Assert.assertNotNull(object3)
    }

    @Test
    @Throws(IOException::class)
    fun updateTest(): Unit = runBlocking {
        val dateMillisSinceEpoch = DateTime.now(DateTimeZone.UTC).millis

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = DiagnosisKeysFileModel.State.None.ordinal,
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
            uniqueKey = "",
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
            subregionList = listOf("subregGion"),
            enVersion = "verSion",
            startUptime = 873454,
            plannedEpoch = 158340,
            startedEpoch = 123456,
            finishedEpoch = 456780,
        )

        exposureDataDao.upsert(
            exposureBaseData = exposureDataBaseModel,
            diagnosisKeysFileList = listOf(diagnosisKeysFileModel),
            exposureSummary = exposureSummaryModel,
            exposureInformationList = mutableListOf(exposureInformation),
            dailySummaryList = mutableListOf(dailySummaryModel),
            exposureWindowList = mutableListOf(exposureWindowAndScanInstancesModel),
        )

        val models = exposureDataDao.getAll()
        Assert.assertEquals(1, models.size)

        models[0].exposureBaseData.finishedEpoch = 6543210
        models[0].diagnosisKeysFileList[0].state = DiagnosisKeysFileModel.State.Downloaded.value
        exposureDataDao.upsert(models[0])

        val allModel = exposureDataDao.getAll()
        Assert.assertEquals(1, allModel.size)

        allModel[0].also { exposureDataAndAllModel ->
            Assert.assertNotNull(exposureDataAndAllModel.exposureBaseData)
            exposureDataAndAllModel.exposureBaseData.also { exposureBaseData ->
                Assert.assertTrue(exposureBaseData.id > 0)
                Assert.assertEquals("rEgion", exposureBaseData.region)
                Assert.assertEquals(listOf("subregGion"), exposureBaseData.subregionList)
                Assert.assertEquals("android", exposureBaseData.platform)
                Assert.assertEquals("verSion", exposureBaseData.enVersion)
                Assert.assertEquals(873454, exposureBaseData.startUptime)
                Assert.assertEquals(158340, exposureBaseData.plannedEpoch)
                Assert.assertEquals(123456, exposureBaseData.startedEpoch)
                Assert.assertEquals(6543210, exposureBaseData.finishedEpoch) //
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
                Assert.assertEquals(
                    DiagnosisKeysFileModel.State.Downloaded.value,
                    diagnosisKeysFile.state
                ) //
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
    fun setTimeoutTest1(): Unit = runBlocking {
        val baseTime = DateTime(2022, 1, 3, 1, 1)

        val baseTimeInMillis = baseTime.millis

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = DiagnosisKeysFileModel.State.Downloaded.ordinal,
            isListed = true,
        )

        val exposureDataBaseModels = listOf(
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                plannedEpoch = baseTimeInMillis + 1,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                plannedEpoch = baseTimeInMillis,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                plannedEpoch = baseTimeInMillis,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 10,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                plannedEpoch = baseTimeInMillis - 1,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 9,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                plannedEpoch = baseTimeInMillis - 2,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
        )

        exposureDataBaseModels.forEach { model ->
            exposureDataDao.upsert(
                exposureBaseData = model,
                diagnosisKeysFileList = mutableListOf(diagnosisKeysFileModel),
            )
        }

        val timeoutDataList = exposureDataDao.findTimeout(
            baseTimeInMillis,
            ExposureDataBaseModel.State.Started.value
        )
        Assert.assertEquals(2, timeoutDataList.size)

        timeoutDataList[0].also { model ->
            Assert.assertEquals(baseTimeInMillis - 1, model.exposureBaseData.plannedEpoch)
            Assert.assertEquals(-1, model.exposureBaseData.finishedEpoch)
        }
        timeoutDataList[1].also { model ->
            Assert.assertEquals(baseTimeInMillis - 2, model.exposureBaseData.plannedEpoch)
            Assert.assertEquals(-1, model.exposureBaseData.finishedEpoch)
        }
    }

    @Test
    @Throws(IOException::class)
    fun setTimeoutTest2(): Unit = runBlocking {
        val baseTime = DateTime(2022, 1, 3, 1, 1)

        val baseTimeInMillis = baseTime.millis

        val diagnosisKeysFileModel = DiagnosisKeysFileModel(
            id = 0,
            exposureDataId = 0,
            region = "Region",
            subregion = "suBregion",
            url = "https://example.com/1234567890",
            created = 121212,
            state = DiagnosisKeysFileModel.State.Downloaded.ordinal,
            isListed = true,
        )

        val exposureDataBaseModels = listOf(
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Finished.value,
                plannedEpoch = baseTimeInMillis,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = 456783,
            ),
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.ResultReceived.value,
                plannedEpoch = baseTimeInMillis,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                plannedEpoch = baseTimeInMillis,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 10,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Started.value,
                plannedEpoch = baseTimeInMillis - 1,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = -1,
            ),
            ExposureDataBaseModel(
                id = 0,
                priority = 11,
                region = "rEgion",
                subregionList = listOf("subregGion"),
                enVersion = "verSion",
                stateValue = ExposureDataBaseModel.State.Finished.value,
                plannedEpoch = baseTimeInMillis - 1,
                startUptime = 24325,
                startedEpoch = 158340,
                finishedEpoch = 456780,
            ),
        )

        exposureDataBaseModels.forEach { model ->
            exposureDataDao.upsert(
                exposureBaseData = model,
                diagnosisKeysFileList = mutableListOf(diagnosisKeysFileModel),
            )
        }

        val timeoutDataList = exposureDataDao.findTimeout(
            baseTimeInMillis,
            ExposureDataBaseModel.State.Started.value
        )
        Assert.assertEquals(1, timeoutDataList.size)

        timeoutDataList[0].also { model ->
            Assert.assertEquals(baseTimeInMillis - 1, model.exposureBaseData.plannedEpoch)
            Assert.assertEquals(-1, model.exposureBaseData.finishedEpoch)
        }

        exposureDataDao.setTimeout(baseTimeInMillis, ExposureDataBaseModel.State.Started.value)

        val filteredList = exposureDataDao.findBy(ExposureDataBaseModel.State.Started.value)
        Assert.assertEquals(1, filteredList.size)

        filteredList[0].also { model ->
            Assert.assertEquals(baseTimeInMillis, model.exposureBaseData.plannedEpoch)
            Assert.assertEquals(-1, model.exposureBaseData.finishedEpoch)
        }

    }
}
