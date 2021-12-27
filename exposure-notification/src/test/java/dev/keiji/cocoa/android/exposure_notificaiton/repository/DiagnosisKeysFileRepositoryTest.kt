package dev.keiji.cocoa.android.exposure_notificaiton.repository

import android.content.Context
import dev.keiji.cocoa.android.common.provider.DateTimeProvider
import dev.keiji.cocoa.android.common.provider.PathProvider
import dev.keiji.cocoa.android.exposure_notificaiton.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notificaiton.entity.DiagnosisKeysFile
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
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

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
    fun getDiagnosisKeysFileListTest_region(): Unit = runBlocking {

        val existDiagnosisKeysFileList = listOf(
            DiagnosisKeysFile(1, "987", null, "https://example.org/1.zip", 23123,
                isProcessed = true,
                isListed = true
            ),
            DiagnosisKeysFile(2, "987", null, "https://example.org/2.zip", 23128,
                isProcessed = true,
                isListed = true
            ),
            DiagnosisKeysFile(3, "987", null, "https://example.org/3.zip", 23132,
                isProcessed = false,
                isListed = true
            ),
            DiagnosisKeysFile(4, "987", null, "https://example.org/4.zip", 23144,
                isProcessed = false,
                isListed = true
            ),
        )

        val listedDiagnosisKeysFileList = listOf(
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/2.zip",
                23128,
            ),
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/3.zip",
                23132,
            ),
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/4.zip",
                23144,
            ),
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/5.zip",
                23177,
            ),
        )

        val expectedDiagnosisKeysFileList = listOf(
            DiagnosisKeysFile(2, "987", null, "https://example.org/2.zip", 23128,
                isProcessed = true,
                isListed = true
            ),
            DiagnosisKeysFile(3, "987", null, "https://example.org/3.zip", 23132,
                isProcessed = false,
                isListed = true
            ),
            DiagnosisKeysFile(4, "987", null, "https://example.org/4.zip", 23144,
                isProcessed = false,
                isListed = true
            ),
            DiagnosisKeysFile(5, "987", null, "https://example.org/5.zip", 23177,
                isProcessed = false,
                isListed = true
            ),
        )

        val mockPathProvider =
            mock<PathProvider> {
                on { diagnosisKeysFileDir() } doReturn tmpFolder.root
            }
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockDiagnosisKeysFileDao =
            mock<DiagnosisKeysFileDao> {
                onBlocking {
                    findAllByRegionAndSubregion(
                        any(),
                        anyOrNull()
                    )
                } doReturn existDiagnosisKeysFileList
                onBlocking {
                    findAllByRegionAndSubregionNotProcessed(
                        any(),
                        anyOrNull()
                    )
                } doReturn expectedDiagnosisKeysFileList
            }
        val mockDiagnosisKeyListProvideServiceApi =
            mock<DiagnosisKeyListProvideServiceApi> {
                onBlocking { getList(any()) } doReturn listedDiagnosisKeysFileList
                onBlocking { getList(any(), any()) } doReturn emptyList()
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

        val actualResult = repository
            .getDiagnosisKeysFileList("987", null)

        Assert.assertEquals(expectedDiagnosisKeysFileList, actualResult)

        verify(mockDiagnosisKeyListProvideServiceApi, times(1)).getList(eq("987"))
        verify(mockDiagnosisKeyListProvideServiceApi, never()).getList(eq("987"), any())

        verify(mockDiagnosisKeysFileDao, times(1)).findAllByRegionAndSubregion(eq("987"), eq(null))
        verify(
            mockDiagnosisKeysFileDao,
            times(1)
        ).findAllByRegionAndSubregionNotProcessed(eq("987"), eq(null))
        verify(mockDiagnosisKeysFileDao, times(1)).insertAll(
            listOf(
                DiagnosisKeysFile(0, "987", null, "https://example.org/5.zip", 23177,
                    isProcessed = false,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).updateAll(
            listOf(
                DiagnosisKeysFile(1, "987", null, "https://example.org/1.zip", 23123,
                    isProcessed = true,
                    isListed = false
                ),
                DiagnosisKeysFile(2, "987", null, "https://example.org/2.zip", 23128,
                    isProcessed = true,
                    isListed = true
                ),
                DiagnosisKeysFile(3, "987", null, "https://example.org/3.zip", 23132,
                    isProcessed = false,
                    isListed = true
                ),
                DiagnosisKeysFile(4, "987", null, "https://example.org/4.zip", 23144,
                    isProcessed = false,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).deleteAll(
            listOf(
                DiagnosisKeysFile(1, "987", null, "https://example.org/1.zip", 23123,
                    isProcessed = true,
                    isListed = false
                ),
            )
        )
    }

    @Test
    fun getDiagnosisKeysFileListTest_region_subregion(): Unit = runBlocking {

        val existDiagnosisKeysFileList = listOf(
            DiagnosisKeysFile(1, "987", "443214", "https://example.org/1.zip", 23123,
                isProcessed = true,
                isListed = true
            ),
            DiagnosisKeysFile(2, "987", "443214", "https://example.org/2.zip", 23128,
                isProcessed = true,
                isListed = true
            ),
            DiagnosisKeysFile(3, "987", "443214", "https://example.org/3.zip", 23132,
                isProcessed = false,
                isListed = true
            ),
            DiagnosisKeysFile(4, "987", "443214", "https://example.org/4.zip", 23144,
                isProcessed = false,
                isListed = true
            ),
        )

        val listedDiagnosisKeysFileList = listOf(
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/2.zip",
                23128,
            ),
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/3.zip",
                23132,
            ),
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/4.zip",
                23144,
            ),
            DiagnosisKeyListProvideServiceApi.Entry(
                987,
                "https://example.org/5.zip",
                23177,
            ),
        )

        val expectedDiagnosisKeysFileList = listOf(
            DiagnosisKeysFile(2, "987", "443214", "https://example.org/2.zip", 23128,
                isProcessed = true,
                isListed = true
            ),
            DiagnosisKeysFile(3, "987", "443214", "https://example.org/3.zip", 23132,
                isProcessed = false,
                isListed = true
            ),
            DiagnosisKeysFile(4, "987", "443214", "https://example.org/4.zip", 23144,
                isProcessed = false,
                isListed = true
            ),
            DiagnosisKeysFile(5, "987", "443214", "https://example.org/5.zip", 23177,
                isProcessed = false,
                isListed = true
            ),
        )

        val mockPathProvider =
            mock<PathProvider> {
                on { diagnosisKeysFileDir() } doReturn tmpFolder.root
            }
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockDiagnosisKeysFileDao =
            mock<DiagnosisKeysFileDao> {
                onBlocking {
                    findAllByRegionAndSubregion(
                        any(),
                        anyOrNull()
                    )
                } doReturn existDiagnosisKeysFileList
                onBlocking {
                    findAllByRegionAndSubregionNotProcessed(
                        any(),
                        anyOrNull()
                    )
                } doReturn expectedDiagnosisKeysFileList
            }
        val mockDiagnosisKeyListProvideServiceApi =
            mock<DiagnosisKeyListProvideServiceApi> {
                onBlocking { getList(any()) } doReturn emptyList()
                onBlocking { getList(any(), any()) } doReturn listedDiagnosisKeysFileList
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

        val actualResult = repository
            .getDiagnosisKeysFileList("987", "443214")

        Assert.assertEquals(expectedDiagnosisKeysFileList, actualResult)

        verify(mockDiagnosisKeyListProvideServiceApi, never()).getList(eq("987"))
        verify(mockDiagnosisKeyListProvideServiceApi, times(1)).getList(eq("987"), eq("443214"))

        verify(mockDiagnosisKeysFileDao, times(1)).findAllByRegionAndSubregion(eq("987"), eq("443214"))
        verify(
            mockDiagnosisKeysFileDao,
            times(1)
        ).findAllByRegionAndSubregionNotProcessed(eq("987"), eq("443214"))
        verify(mockDiagnosisKeysFileDao, times(1)).insertAll(
            listOf(
                DiagnosisKeysFile(0, "987", "443214", "https://example.org/5.zip", 23177,
                    isProcessed = false,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).updateAll(
            listOf(
                DiagnosisKeysFile(1, "987", "443214", "https://example.org/1.zip", 23123,
                    isProcessed = true,
                    isListed = false
                ),
                DiagnosisKeysFile(2, "987", "443214", "https://example.org/2.zip", 23128,
                    isProcessed = true,
                    isListed = true
                ),
                DiagnosisKeysFile(3, "987", "443214", "https://example.org/3.zip", 23132,
                    isProcessed = false,
                    isListed = true
                ),
                DiagnosisKeysFile(4, "987", "443214", "https://example.org/4.zip", 23144,
                    isProcessed = false,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).deleteAll(
            listOf(
                DiagnosisKeysFile(1, "987", "443214", "https://example.org/1.zip", 23123,
                    isProcessed = true,
                    isListed = false
                ),
            )
        )
    }
}
