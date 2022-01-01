package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.keiji.cocoa.android.exposure_notification.AppDatabase
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureInformation
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureInformationDao
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
class ExposureInformationModelTest {
    companion object {
        private const val DELTA = 0.0000001
    }

    private lateinit var db: AppDatabase

    private lateinit var exposureInformationDao: ExposureInformationDao

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        exposureInformationDao = db.exposureInformationDao()
    }

    @After
    @Throws(IOException::class)
    fun disposeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectTest(): Unit = runBlocking {
        val dateMillisSinceEpoch = DateTime(DateTimeZone.UTC).millis

        val exposureInformation = ExposureInformationModel(
            id = 0,
            exposureDataId = 0,
            attenuationDurationsInMillis = IntArray(8) { index -> index + 1 /* dummy values */},
            attenuationValue = 20,
            dateMillisSinceEpoch = dateMillisSinceEpoch,
            durationInMillis = 100.0,
            totalRiskScore = 10,
            transmissionRiskLevel = 4,
        )
        val id = exposureInformationDao.insert(exposureInformation)
        Assert.assertTrue(id > 0)

        val allList = exposureInformationDao.getAll()
        Assert.assertEquals(1, allList.size)

        allList[0].also { model ->
            Assert.assertTrue(model.id > 0)
            Assert.assertEquals(8, model.attenuationDurationsInMillis.size)
            model.attenuationDurationsInMillis.also { attenuationDurationsInMillis ->
                Assert.assertEquals(1, attenuationDurationsInMillis[0])
                Assert.assertEquals(2, attenuationDurationsInMillis[1])
                Assert.assertEquals(3, attenuationDurationsInMillis[2])
                Assert.assertEquals(4, attenuationDurationsInMillis[3])
                Assert.assertEquals(5, attenuationDurationsInMillis[4])
                Assert.assertEquals(6, attenuationDurationsInMillis[5])
                Assert.assertEquals(7, attenuationDurationsInMillis[6])
                Assert.assertEquals(8, attenuationDurationsInMillis[7])
            }
            Assert.assertEquals(20, model.attenuationValue)
            Assert.assertEquals(dateMillisSinceEpoch, model.dateMillisSinceEpoch)
            Assert.assertEquals(100.0, model.durationInMillis, DELTA)
            Assert.assertEquals(10, model.totalRiskScore)
            Assert.assertEquals(4, model.transmissionRiskLevel)
        }
    }
}
