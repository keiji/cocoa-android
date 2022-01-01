package dev.keiji.cocoa.android.exposure_notification.model

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.keiji.cocoa.android.exposure_notification.AppDatabase
import dev.keiji.cocoa.android.exposure_notification.dao.DiagnosisKeysFileDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DiagnosisKeysFileModelTest {
    companion object {
        private const val DELTA = 0.0000001
    }

    private lateinit var db: AppDatabase

    private lateinit var diagnosisKeysFileDao: DiagnosisKeysFileDao

    @Before
    fun createDb() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java).build()
        diagnosisKeysFileDao = db.diagnosisKeysFileDao()
    }

    @After
    @Throws(IOException::class)
    fun disposeDb() {
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun createOneObjectTest(): Unit = runBlocking {
        val diagnosisKeysFileList = listOf(
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion",
                url = "https://examaple.com/a/b/c/",
                created = 11112222,
                state = State.Processing.value,
                isListed = true,
            )
        )

        val ids = diagnosisKeysFileDao.insertAll(diagnosisKeysFileList)
        Assert.assertEquals(1, ids.size)

        val allList = diagnosisKeysFileDao.getAll()
        Assert.assertEquals(1, allList.size)

        allList[0].also { diagnosisKeysFileModel ->
            Assert.assertTrue(diagnosisKeysFileModel.id > 0)
            Assert.assertEquals(0, diagnosisKeysFileModel.exposureDataId)
            Assert.assertEquals("regIon", diagnosisKeysFileModel.region)
            Assert.assertEquals("subrEgion", diagnosisKeysFileModel.subregion)
            Assert.assertEquals("https://examaple.com/a/b/c/", diagnosisKeysFileModel.url)
            Assert.assertEquals(11112222, diagnosisKeysFileModel.created)
            Assert.assertEquals(State.Processing.value, diagnosisKeysFileModel.state)
            Assert.assertEquals(true, diagnosisKeysFileModel.isListed)
        }
    }

    @Test
    @Throws(IOException::class)
    fun findByTest1(): Unit = runBlocking {
        val diagnosisKeysFileList = listOf(
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion",
                url = "https://examaple.com/a/b/c/1",
                created = 11112222,
                state = State.Completed.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = null,
                url = "https://examaple.com/a/b/c/1",
                created = 11112222,
                state = State.Completed.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon2",
                subregion = "subrEgion1",
                url = "https://examaple.com/a/b/c/2",
                created = 11112222,
                state = State.Processing.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon3",
                subregion = "subrEgion1",
                url = "https://examaple.com/a/b/c/3",
                created = 11112222,
                state = State.Completed.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon2",
                subregion = "subrEgion2",
                url = "https://examaple.com/a/b/c/4",
                created = 11112222,
                state = State.Processing.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion3",
                url = "https://examaple.com/a/b/c/5",
                created = 11112222,
                state = State.Processing.value,
                isListed = true,
            ),
        )

        val ids = diagnosisKeysFileDao.insertAll(diagnosisKeysFileList)
        Assert.assertEquals(6, ids.size)

        val filteredList1 = diagnosisKeysFileDao.findAllBy("regIon")
        Assert.assertEquals(3, filteredList1.size)
        filteredList1.forEach { diagnosisKeysFileModel ->
            Assert.assertEquals("regIon", diagnosisKeysFileModel.region)
        }

        val filteredList2 = diagnosisKeysFileDao.findAllBy("regIon2")
        Assert.assertEquals(2, filteredList2.size)
        filteredList2.forEach { diagnosisKeysFileModel ->
            Assert.assertEquals("regIon2", diagnosisKeysFileModel.region)
        }

        val filteredList3 = diagnosisKeysFileDao.findAllBy("regIon", null)
        Assert.assertEquals(1, filteredList3.size)
        filteredList3.forEach { diagnosisKeysFileModel ->
            Assert.assertEquals("regIon", diagnosisKeysFileModel.region)
            Assert.assertNull(diagnosisKeysFileModel.subregion)
        }

        val filteredList4 = diagnosisKeysFileDao.findAllBy("regIon2", "subrEgion2")
        Assert.assertEquals(1, filteredList4.size)
        filteredList4.forEach { diagnosisKeysFileModel ->
            Assert.assertEquals("regIon2", diagnosisKeysFileModel.region)
            Assert.assertEquals("subrEgion2", diagnosisKeysFileModel.subregion)
        }
    }

    @Test
    @Throws(IOException::class)
    fun findNotCompletedTest(): Unit = runBlocking {
        val diagnosisKeysFileList = listOf(
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion",
                url = "https://examaple.com/a/b/c/1",
                created = 11112222,
                state = State.Completed.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion",
                url = "https://examaple.com/a/b/c/2",
                created = 11112222,
                state = State.Processing.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion",
                url = "https://examaple.com/a/b/c/3",
                created = 11112222,
                state = State.Completed.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion",
                url = "https://examaple.com/a/b/c/4",
                created = 11112222,
                state = State.Processing.value,
                isListed = true,
            ),
            DiagnosisKeysFileModel(
                id = 0,
                exposureDataId = 0,
                region = "regIon",
                subregion = "subrEgion",
                url = "https://examaple.com/a/b/c/5",
                created = 11112222,
                state = State.Processing.value,
                isListed = true,
            ),
        )

        val ids = diagnosisKeysFileDao.insertAll(diagnosisKeysFileList)
        Assert.assertEquals(5, ids.size)

        val allList = diagnosisKeysFileDao.findNotCompleted("regIon")
        Assert.assertEquals(3, allList.size)

        allList.forEach { diagnosisKeysFileModel ->
            Assert.assertTrue(State.Completed.value > diagnosisKeysFileModel.state)
        }
    }
}
