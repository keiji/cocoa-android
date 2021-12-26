package dev.keiji.cocoa.android.exposure_notificaiton.repository

import android.content.Context
import dev.keiji.cocoa.android.common.provider.DateTimeProvider
import dev.keiji.cocoa.android.common.provider.PathProvider
import dev.keiji.cocoa.android.exposure_notificaiton.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notification.api.DiagnosisKeyFileProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.api.DiagnosisKeyListProvideServiceApi
import kotlinx.coroutines.runBlocking
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

@RunWith(MockitoJUnitRunner.Silent::class)
class DiagnosisKeysFileRepositoryTest {

    private lateinit var tmpFolder: TemporaryFolder

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        tmpFolder = TemporaryFolder().also {
            it.create()
        }
    }

    @After
    fun tearDown() {
        tmpFolder.delete()
    }

    @Test
    fun getDiagnosisKeysFileListTest(): Unit = runBlocking {
        val mockPathProvider =
            mock<PathProvider> {
                on { diagnosisKeysFileDir() } doReturn tmpFolder.root
            }
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockDiagnosisKeysFileDao =
            mock<DiagnosisKeysFileDao> {
            }
        val mockDiagnosisKeyListProvideServiceApi =
            mock<DiagnosisKeyListProvideServiceApi> {
            }
        val mockDiagnosisKeyFileProvideServiceApi =
            mock<DiagnosisKeyFileProvideServiceApi> {
            }

        val repository = DiagnosisKeysFileRepositoryImpl(
            mockContext,
            mockPathProvider,
            mockDateTimeProvider,
            mockDiagnosisKeysFileDao,
            mockDiagnosisKeyListProvideServiceApi,
            mockDiagnosisKeyFileProvideServiceApi,
        )

        val diagnosisKeysFileList = repository.getDiagnosisKeysFileList("987", null)
        Assert.assertNotNull(diagnosisKeysFileList)
    }
}