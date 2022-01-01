package dev.keiji.cocoa.android.exposure_notification.dao

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.keiji.cocoa.android.exposure_notification.AppDatabase
import dev.keiji.cocoa.android.exposure_notification.model.ExposureSummaryModel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ExposureSummaryDaoTest {
    companion object {
        private const val DELTA = 0.0000001
    }

    private lateinit var db: AppDatabase

    private lateinit var exposureSummaryDao: ExposureSummaryDao

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        exposureSummaryDao = db.exposureSummaryDao()
    }

    @After
    @Throws(IOException::class)
    fun disposeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectTest(): Unit = runBlocking {
        val exposureSummaryModel = ExposureSummaryModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(3) { index -> 20 * (index + 1) /* dummy values */ },
            daysSinceLastExposure = 2,
            matchedKeyCount = 4,
            maximumRiskScore = 100,
            summationRiskScore = 10,
        )
        val id = exposureSummaryDao.insert(exposureSummaryModel)
        Assert.assertTrue(id > 0)

        val allList = exposureSummaryDao.getAll()
        Assert.assertEquals(1, allList.size)

        allList[0].also { model ->
            Assert.assertTrue(model.id > 0)
            Assert.assertEquals(3, model.attenuationDurationsInMillis.size)
            model.attenuationDurationsInMillis.also { attenuationDurationsInMillis ->
                Assert.assertEquals(20, attenuationDurationsInMillis[0])
                Assert.assertEquals(40, attenuationDurationsInMillis[1])
                Assert.assertEquals(60, attenuationDurationsInMillis[2])
            }
            Assert.assertEquals(2, model.daysSinceLastExposure)
            Assert.assertEquals(4, model.matchedKeyCount)
            Assert.assertEquals(100, model.maximumRiskScore)
            Assert.assertEquals(10, model.summationRiskScore)
        }
    }
}
