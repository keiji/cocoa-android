package dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository

import android.content.Context
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureConfigurationApi
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert
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
import org.mockito.kotlin.doThrow
import java.io.File
import java.io.IOException
import java.lang.IllegalStateException

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@RunWith(MockitoJUnitRunner.Silent::class)
class ExposureConfigurationRepositoryTest {
    companion object {
        private const val FILENAME = "exposure_configuration.json"
        private const val FILENAME_FALLBACK = "exposure_configuration_fallback.json"
        private const val FILENAME_BROKEN = "exposure_configuration_broken.json"
    }

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var tmpFolder: TemporaryFolder

    private lateinit var exposureNotificationFile: File
    private lateinit var fallbackExposureNotificationFile: File
    private lateinit var brokenExposureNotificationFile: File

    private lateinit var expectedExposureConfiguration: ExposureConfiguration
    private lateinit var fallbackExposureConfiguration: ExposureConfiguration

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
        expectedExposureConfiguration = Json.decodeFromString<ExposureConfiguration>(json)
            .apply {
                appleExposureConfigV1 = null
                appleExposureConfigV2 = null
            }
        exposureNotificationFile = tmpFolder.newFile().also {
            it.writeText(json)
        }

        val fallbackJson =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME_FALLBACK)).use { isr ->
                isr.readText()
            }
        fallbackExposureConfiguration = Json.decodeFromString<ExposureConfiguration>(fallbackJson)
            .apply {
                appleExposureConfigV1 = null
                appleExposureConfigV2 = null
            }
        fallbackExposureNotificationFile = tmpFolder.newFile().also {
            it.writeText(fallbackJson)
        }

        val brokenJson =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME_BROKEN)).use { isr ->
                isr.readText()
            }
        brokenExposureNotificationFile = tmpFolder.newFile().also {
            it.writeText(brokenJson)
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
                on { exposureConfigurationFile() } doReturn tmpFolder.newFile()
            }
        val mockExposureConfigurationProvideServiceApi =
            mock<ExposureConfigurationApi> {
                onBlocking { downloadConfigurationFile(any(), any()) } doReturn exposureNotificationFile
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
        Assert.assertEquals(expectedExposureConfiguration, exposureConfiguration)
    }

    @Test
    fun loadExposureConfiguration_downloadFailed(): Unit = runBlocking {
        val mockPathProvider =
            mock<PathSource> {
                on { exposureConfigurationDir() } doReturn tmpFolder.root
                on { exposureConfigurationFile() } doReturn fallbackExposureNotificationFile
            }
        val mockConfigurationSource =
            mock<ConfigurationSource> {
                onBlocking { exposureConfigurationUrl() } doReturn "https://example.com"
            }
        val mockExposureConfigurationProvideServiceApi =
            mock<ExposureConfigurationApi> {
                onBlocking { downloadConfigurationFile(any(), any()) } doThrow IOException()
            }

        val repository = ExposureConfigurationRepositoryImpl(
            mockContext,
            mockPathProvider,
            mockExposureConfigurationProvideServiceApi,
            mockConfigurationSource,
        )

        val exposureConfiguration = repository.getExposureConfiguration()
        Assert.assertEquals(fallbackExposureConfiguration, exposureConfiguration)
    }

    @Test
    fun loadExposureConfiguration_cannotFallback(): Unit = runBlocking {
        val mockPathProvider =
            mock<PathSource> {
                on { exposureConfigurationDir() } doReturn tmpFolder.root
                on { exposureConfigurationFile() } doReturn tmpFolder.newFile()
            }
        val mockConfigurationSource =
            mock<ConfigurationSource> {
                onBlocking { exposureConfigurationUrl() } doReturn "https://example.com"
            }
        val mockExposureConfigurationProvideServiceApi =
            mock<ExposureConfigurationApi> {
                onBlocking { downloadConfigurationFile(any(), any()) } doThrow IOException()
            }

        val repository = ExposureConfigurationRepositoryImpl(
            mockContext,
            mockPathProvider,
            mockExposureConfigurationProvideServiceApi,
            mockConfigurationSource,
        )

        try {
            val exposureConfiguration = repository.getExposureConfiguration()
            Assert.fail()
        } catch (e: IllegalStateException) {
        }
    }

    @Test
    fun loadExposureConfiguration_brokenJsonFile(): Unit = runBlocking {
        val mockPathProvider =
            mock<PathSource> {
                on { exposureConfigurationDir() } doReturn tmpFolder.root
                on { exposureConfigurationFile() } doReturn fallbackExposureNotificationFile
            }
        val mockExposureConfigurationProvideServiceApi =
            mock<ExposureConfigurationApi> {
                onBlocking { downloadConfigurationFile(any(), any()) } doReturn brokenExposureNotificationFile
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
        Assert.assertEquals(fallbackExposureConfiguration, exposureConfiguration)
    }

}