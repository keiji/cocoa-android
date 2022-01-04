package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.keiji.cocoa.android.exposure_notification.AppDatabase
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.Infectiousness
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ReportType
import dev.keiji.cocoa.android.exposure_notification.model.ExposureWindowModel
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
class ExposureWindowDaoTest {
    private lateinit var db: AppDatabase

    private lateinit var exposureWindowDao: ExposureWindowDao

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        exposureWindowDao = db.exposureWindowDao()
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

        val exposureWindowModels = listOf(
            ExposureWindowModel(
                id = 0,
                exposureDataId = 0,
                uniqueKey = "",
                calibrationConfidence = 2,
                dateMillisSinceEpoch = dateMillisSinceEpoch,
                infectiousness = Infectiousness.STANDARD.ordinal,
                reportType = ReportType.CONFIRMED_TEST.ordinal,
            ),
            ExposureWindowModel(
                id = 0,
                exposureDataId = 0,
                uniqueKey = "",
                calibrationConfidence = 2,
                dateMillisSinceEpoch = dateMillisSinceEpoch,
                infectiousness = Infectiousness.HIGH.ordinal,
                reportType = ReportType.CONFIRMED_CLINICAL_DIAGNOSIS.ordinal,
            ),
        )
        val ids = exposureWindowDao.insertAll(exposureWindowModels)
        Assert.assertEquals(2, ids.size)

        val allList = exposureWindowDao.getAll()
        Assert.assertEquals(2, allList.size)

        allList[0].also { model ->
            Assert.assertTrue(model.exposureWindowModel.id > 0)
            Assert.assertEquals(2, model.exposureWindowModel.calibrationConfidence)
            Assert.assertEquals(
                dateMillisSinceEpoch,
                model.exposureWindowModel.dateMillisSinceEpoch
            )
            Assert.assertEquals(
                Infectiousness.STANDARD.ordinal,
                model.exposureWindowModel.infectiousness
            )
            Assert.assertEquals(
                ReportType.CONFIRMED_TEST.ordinal,
                model.exposureWindowModel.reportType
            )
        }
        allList[1].also { model ->
            Assert.assertTrue(model.exposureWindowModel.id > 0)
            Assert.assertEquals(2, model.exposureWindowModel.calibrationConfidence)
            Assert.assertEquals(
                dateMillisSinceEpoch,
                model.exposureWindowModel.dateMillisSinceEpoch
            )
            Assert.assertEquals(
                Infectiousness.HIGH.ordinal,
                model.exposureWindowModel.infectiousness
            )
            Assert.assertEquals(
                ReportType.CONFIRMED_CLINICAL_DIAGNOSIS.ordinal,
                model.exposureWindowModel.reportType
            )
        }
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectWithRelationTest(): Unit = runBlocking {

        val dateMillisSinceEpoch = DateTime.now(DateTimeZone.UTC).millis

        val exposureWindowModel = ExposureWindowModel(
            id = 0,
            exposureDataId = 0,
            uniqueKey = "",
            calibrationConfidence = 2,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            infectiousness = Infectiousness.STANDARD.ordinal,
            reportType = ReportType.CONFIRMED_TEST.ordinal,
        )
        val scanInstanceModels = listOf(
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

        exposureWindowDao.insert(exposureWindowModel, scanInstanceModels)

        val allList = exposureWindowDao.getAll()
        Assert.assertEquals(1, allList.size)

        allList[0].also { model ->
            Assert.assertTrue(model.exposureWindowModel.id > 0)
            Assert.assertEquals(2, model.exposureWindowModel.calibrationConfidence)
            Assert.assertEquals(
                dateMillisSinceEpoch,
                model.exposureWindowModel.dateMillisSinceEpoch
            )
            Assert.assertEquals(
                Infectiousness.STANDARD.ordinal,
                model.exposureWindowModel.infectiousness
            )
            Assert.assertEquals(
                ReportType.CONFIRMED_TEST.ordinal,
                model.exposureWindowModel.reportType
            )

            Assert.assertEquals(
                2,
                model.scanInstances.size
            )
            model.scanInstances[0].also { scanInstanceModel ->
                Assert.assertTrue(scanInstanceModel.id > 0)
                Assert.assertEquals(
                    model.exposureWindowModel.id,
                    scanInstanceModel.exposureWindowId
                )
                Assert.assertEquals(10, scanInstanceModel.minAttenuationDb)
                Assert.assertEquals(20, scanInstanceModel.secondsSinceLastScan)
                Assert.assertEquals(30, scanInstanceModel.typicalAttenuationDb)
            }
            model.scanInstances[1].also { scanInstanceModel ->
                Assert.assertTrue(scanInstanceModel.id > 0)
                Assert.assertEquals(
                    model.exposureWindowModel.id,
                    scanInstanceModel.exposureWindowId
                )
                Assert.assertEquals(11, scanInstanceModel.minAttenuationDb)
                Assert.assertEquals(21, scanInstanceModel.secondsSinceLastScan)
                Assert.assertEquals(31, scanInstanceModel.typicalAttenuationDb)
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun existTest1(): Unit = runBlocking {

        val dateMillisSinceEpoch = DateTime.now(DateTimeZone.UTC).millis

        val exposureWindowModel = ExposureWindowModel(
            id = 0,
            exposureDataId = 0,
            uniqueKey = "abcdefghijklmn",
            calibrationConfidence = 2,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            infectiousness = Infectiousness.STANDARD.ordinal,
            reportType = ReportType.CONFIRMED_TEST.ordinal,
        )
        val scanInstanceModels = listOf(
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

        exposureWindowDao.insert(exposureWindowModel, scanInstanceModels)

        Assert.assertTrue(exposureWindowDao.exist("abcdefghijklmn"))
        Assert.assertFalse(exposureWindowDao.exist("42342342323"))
    }
}
