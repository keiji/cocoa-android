package dev.keiji.cocoa.android

import android.app.Activity
import android.content.Context
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.common.source.DateTimeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class ExposureNotificationWrapperMockTest {
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var tmpFolder: TemporaryFolder

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)

        tmpFolder = TemporaryFolder().also {
            it.create()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()

        tmpFolder.delete()
    }

    @Test
    fun blankTest() = runBlocking {
        val mockPathSource =
            mock<PathSource> {
                on { diagnosisKeysFileDir() } doReturn tmpFolder.root
            }
        val mockDateTimeSource =
            mock<DateTimeSource> {
                on { utcNow() } doReturn DateTime.now(DateTimeZone.UTC)
            }
        val mockExposureDetectionService =
            mock<ExposureDetectionService> {
            }

        val mockActivity =
            mock<Activity> {
            }

        val target = ExposureNotificationWrapperMock(
            mockContext,
            mockDateTimeSource,
            mockPathSource,
            mockExposureDetectionService
        )

        val dummyTemporaryExposureKeyList = target.getTemporaryExposureKeyHistory(mockActivity)
        Assert.assertNotNull(dummyTemporaryExposureKeyList)
        Assert.assertTrue(dummyTemporaryExposureKeyList.isNotEmpty())
    }
}
