package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.keiji.cocoa.android.exposure_notification.AppDatabase
import dev.keiji.cocoa.android.exposure_notification.model.DailySummaryModel
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryDataModel
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
class DailySummaryDaoTest {
    companion object {
        private const val DELTA = 0.0000001
    }

    private lateinit var db: AppDatabase

    private lateinit var dailySummaryDao: DailySummaryDao

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        dailySummaryDao = db.dailySummaryDao()
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

        val id = dailySummaryDao.insert(dailySummaryModel)
        Assert.assertTrue(id > 0)

        val allList = dailySummaryDao.getAll()
        Assert.assertEquals(1, allList.size)

        allList[0].also { model ->
            Assert.assertTrue(model.id > 0)
            Assert.assertEquals(dateMillisSinceEpoch, model.dateMillisSinceEpoch)
            Assert.assertNull(model.summaryData)
            Assert.assertNull(model.confirmedClinicalDiagnosisSummary)
            Assert.assertNull(model.confirmedTestSummary)
            Assert.assertNull(model.recursiveSummary)
            Assert.assertNull(model.selfReportedSummary)
        }
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectWithExposureSummaryDataTest(): Unit = runBlocking {
        val dateMillisSinceEpoch = DateTime.now(DateTimeZone.UTC).millis

        val dailySummaryModel = DailySummaryModel(
            id = 0,
            exposureDataId = 0,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            summaryData = ExposureSummaryDataModel(
                maximumScore = 1.0,
                scoreSum = 20.1,
                weightedDurationSum = 20.0,
            ),
            confirmedClinicalDiagnosisSummary = ExposureSummaryDataModel(
                maximumScore = 1.1,
                scoreSum = 20.2,
                weightedDurationSum = 20.1,
            ),
            confirmedTestSummary = ExposureSummaryDataModel(
                maximumScore = 1.2,
                scoreSum = 20.3,
                weightedDurationSum = 20.2,
            ),
            recursiveSummary = ExposureSummaryDataModel(
                maximumScore = 1.3,
                scoreSum = 20.4,
                weightedDurationSum = 20.3,
            ),
            selfReportedSummary = ExposureSummaryDataModel(
                maximumScore = 1.4,
                scoreSum = 20.5,
                weightedDurationSum = 20.4,
            ),
        )

        val id = dailySummaryDao.insert(dailySummaryModel)
        Assert.assertTrue(id > 0)

        val allList = dailySummaryDao.getAll()
        Assert.assertEquals(1, allList.size)

        allList[0].also { model ->
            Assert.assertTrue(model.id > 0)
            Assert.assertEquals(dateMillisSinceEpoch, model.dateMillisSinceEpoch)

            Assert.assertNotNull(model.summaryData)
            Assert.assertNotNull(model.confirmedClinicalDiagnosisSummary)
            Assert.assertNotNull(model.confirmedTestSummary)
            Assert.assertNotNull(model.recursiveSummary)
            Assert.assertNotNull(model.selfReportedSummary)

            model.summaryData!!.also { dailySummaryDao ->
                Assert.assertEquals(1.0, dailySummaryDao.maximumScore, DELTA)
                Assert.assertEquals(20.1, dailySummaryDao.scoreSum, DELTA)
                Assert.assertEquals(20.0, dailySummaryDao.weightedDurationSum, DELTA)
            }
            model.confirmedClinicalDiagnosisSummary!!.also { dailySummaryDao ->
                Assert.assertEquals(1.1, dailySummaryDao.maximumScore, DELTA)
                Assert.assertEquals(20.2, dailySummaryDao.scoreSum, DELTA)
                Assert.assertEquals(20.1, dailySummaryDao.weightedDurationSum, DELTA)
            }
            model.confirmedTestSummary!!.also { dailySummaryDao ->
                Assert.assertEquals(1.2, dailySummaryDao.maximumScore, DELTA)
                Assert.assertEquals(20.3, dailySummaryDao.scoreSum, DELTA)
                Assert.assertEquals(20.2, dailySummaryDao.weightedDurationSum, DELTA)
            }
            model.recursiveSummary!!.also { dailySummaryDao ->
                Assert.assertEquals(1.3, dailySummaryDao.maximumScore, DELTA)
                Assert.assertEquals(20.4, dailySummaryDao.scoreSum, DELTA)
                Assert.assertEquals(20.3, dailySummaryDao.weightedDurationSum, DELTA)
            }
            model.selfReportedSummary!!.also { dailySummaryDao ->
                Assert.assertEquals(1.4, dailySummaryDao.maximumScore, DELTA)
                Assert.assertEquals(20.5, dailySummaryDao.scoreSum, DELTA)
                Assert.assertEquals(20.4, dailySummaryDao.weightedDurationSum, DELTA)
            }
        }
    }
}
