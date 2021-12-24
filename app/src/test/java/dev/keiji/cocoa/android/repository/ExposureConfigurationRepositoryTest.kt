package dev.keiji.cocoa.android.repository

import android.content.Context
import dev.keiji.cocoa.android.api.ExposureConfigurationProvideServiceApi
import kotlinx.coroutines.runBlocking
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

@RunWith(MockitoJUnitRunner::class)
class ExposureConfigurationRepositoryTest {

    private lateinit var tmpFolder: TemporaryFolder

    private lateinit var exposureNotificationFile: File

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        tmpFolder = TemporaryFolder().also {
            it.create()
        }

        val json =
            InputStreamReader(javaClass.classLoader!!.getResourceAsStream(FILENAME)).use { isr ->
                isr.readText()
            }
        exposureNotificationFile = tmpFolder.newFile(FILENAME).also {
            it.writeText(json)
        }
    }

    @After
    fun tearDown() {
        tmpFolder.delete()
    }

    @Test
    fun loadExposureConfiguration() {
        val mockExposureConfigurationProvideServiceApi =
            mock<ExposureConfigurationProvideServiceApi> {
                onBlocking { getConfiguration(any(), any()) } doReturn exposureNotificationFile
            }

        val repository = ExposureConfigurationRepositoryImpl(
            mockContext,
            mockExposureConfigurationProvideServiceApi
        )

        runBlocking {
            val exposureConfiguration = repository.getExposureConfiguration()
            assertNotNull(exposureConfiguration)
        }
    }
}