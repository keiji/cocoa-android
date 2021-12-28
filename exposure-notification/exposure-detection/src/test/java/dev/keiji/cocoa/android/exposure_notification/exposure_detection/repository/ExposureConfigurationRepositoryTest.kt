package dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository

import android.content.Context
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureConfigurationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.InputStreamReader
import org.junit.rules.TemporaryFolder
import java.io.File

private const val FILENAME = "exposure_configuration.json"

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class ExposureConfigurationRepositoryTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var tmpFolder: TemporaryFolder

    private lateinit var exposureNotificationFile: File

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)

        tmpFolder = TemporaryFolder().also {
            it.create()
        }

        val json =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }
        exposureNotificationFile = tmpFolder.newFile().also {
            it.writeText(json)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()

        tmpFolder.delete()
    }

    @Test
    fun loadExposureConfiguration(): Unit = runBlocking {
        val mockPathProvider =
            mock<PathSource> {
                on { exposureConfigurationDir() } doReturn tmpFolder.root
            }
        val mockExposureConfigurationProvideServiceApi =
            mock<ExposureConfigurationApi> {
                onBlocking { getConfiguration(any(), any()) } doReturn exposureNotificationFile
            }
        val mockConfigurationSource =
            mock<ConfigurationSource> {
                onBlocking { exposureConfigurationUrl() } doReturn "https://example.com"
            }

        val repository = ExposureConfigurationRepositoryImpl(
            mockContext,
            mockPathProvider,
            mockExposureConfigurationProvideServiceApi,
            mockConfigurationSource,
        )

        val exposureConfiguration = repository.getExposureConfiguration()
        assertNotNull(exposureConfiguration)
    }
}