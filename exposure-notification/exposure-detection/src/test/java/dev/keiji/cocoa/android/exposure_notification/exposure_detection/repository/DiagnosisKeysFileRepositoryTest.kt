package dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository

import android.content.Context
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyFileApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyListApi
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
            DiagnosisKeysFileModel(
                1, 1, "987", null, "https://example.org/987/1.zip", 23123,
                state = DiagnosisKeysFileModel.State.Completed.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                2, 1, "987", null, "https://example.org/987/2.zip", 23128,
                state = DiagnosisKeysFileModel.State.Completed.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                3, 1, "987", null, "https://example.org/987/3.zip", 23132,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                4, 1, "987", null, "https://example.org/987/4.zip", 23144,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
        )

        val listedDiagnosisKeysFileList = listOf(
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/2.zip",
                23128,
            ),
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/3.zip",
                23132,
            ),
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/4.zip",
                23144,
            ),
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/5.zip",
                23177,
            ),
        )

        val expectedDiagnosisKeysFileList = listOf(
            DiagnosisKeysFileModel(
                2, 0, "987", null, "https://example.org/987/2.zip", 23128,
                state = DiagnosisKeysFileModel.State.Completed.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                3, 0, "987", null, "https://example.org/987/3.zip", 23132,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                4, 0, "987", null, "https://example.org/987/4.zip", 23144,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                5, 0, "987", null, "https://example.org/987/5.zip", 23177,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
        )

        val mockPathSource =
            mock<PathSource> {
                on { diagnosisKeysFileDir() } doReturn tmpFolder.root
            }
        val mockDateTimeSource =
            mock<DateTimeSource> {
            }
        val mockDiagnosisKeysFileDao =
            mock<DiagnosisKeysFileDao> {
                onBlocking {
                    findAllBy(
                        any(),
                        anyOrNull()
                    )
                } doReturn existDiagnosisKeysFileList
                onBlocking {
                    findNotCompleted(
                        any(),
                        anyOrNull()
                    )
                } doReturn expectedDiagnosisKeysFileList
            }
        val mockDiagnosisKeyListProvideServiceApi =
            mock<DiagnosisKeyListApi> {
                onBlocking { getList(any()) } doReturn listedDiagnosisKeysFileList
                onBlocking { getList(any(), any()) } doReturn emptyList()
            }
        val mockDiagnosisKeyFileProvideServiceApi =
            mock<DiagnosisKeyFileApi> {
            }

        val repository = DiagnosisKeysFileRepositoryImpl(
            mockContext,
            mockPathSource,
            mockDateTimeSource,
            mockDiagnosisKeysFileDao,
            mockDiagnosisKeyListProvideServiceApi,
            mockDiagnosisKeyFileProvideServiceApi,
        )

        val actualResult = repository
            .getDiagnosisKeysFileList("987", null)

        Assert.assertEquals(expectedDiagnosisKeysFileList, actualResult)

        verify(mockDiagnosisKeyListProvideServiceApi, times(1)).getList(eq("987"))
        verify(mockDiagnosisKeyListProvideServiceApi, never()).getList(eq("987"), any())

        verify(mockDiagnosisKeysFileDao, times(1)).findAllBy(eq("987"), eq(null))
        verify(
            mockDiagnosisKeysFileDao,
            times(1)
        ).findNotCompleted(eq("987"), eq(null))

        verify(mockDiagnosisKeysFileDao, times(1)).insertAll(
            listOf(
                DiagnosisKeysFileModel(
                    0, 0, "987", null, "https://example.org/987/5.zip", 23177,
                    state = DiagnosisKeysFileModel.State.None.value,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).updateAll(
            listOf(
                DiagnosisKeysFileModel(
                    2, 1, "987", null, "https://example.org/987/2.zip", 23128,
                    state = DiagnosisKeysFileModel.State.Completed.value,
                    isListed = true
                ),
                DiagnosisKeysFileModel(
                    3, 1, "987", null, "https://example.org/987/3.zip", 23132,
                    state = DiagnosisKeysFileModel.State.None.value,
                    isListed = true
                ),
                DiagnosisKeysFileModel(
                    4, 1, "987", null, "https://example.org/987/4.zip", 23144,
                    state = DiagnosisKeysFileModel.State.None.value,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).deleteAll(
            listOf(
                DiagnosisKeysFileModel(
                    1, 1, "987", null, "https://example.org/987/1.zip", 23123,
                    state = DiagnosisKeysFileModel.State.Completed.value,
                    isListed = false
                ),
            )
        )
    }

    @Test
    fun getDiagnosisKeysFileListTest_region_subregion(): Unit = runBlocking {

        val existDiagnosisKeysFileList = listOf(
            DiagnosisKeysFileModel(
                1, 1, "987", "443214", "https://example.org/987/443214/1.zip", 23123,
                state = DiagnosisKeysFileModel.State.Completed.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                2, 1, "987", "443214", "https://example.org/987/443214/2.zip", 23128,
                state = DiagnosisKeysFileModel.State.Completed.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                3, 1, "987", "443214", "https://example.org/987/443214/3.zip", 23132,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                4, 1, "987", "443214", "https://example.org/987/443214/4.zip", 23144,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
        )

        val listedDiagnosisKeysFileList = listOf(
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/443214/2.zip",
                23128,
            ),
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/443214/3.zip",
                23132,
            ),
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/443214/4.zip",
                23144,
            ),
            DiagnosisKeyListApi.Entry(
                987,
                "https://example.org/987/443214/5.zip",
                23177,
            ),
        )

        val expectedDiagnosisKeysFileList = listOf(
            DiagnosisKeysFileModel(
                2, 1, "987", "443214", "https://example.org/987/443214/2.zip", 23128,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                3, 1, "987", "443214", "https://example.org/987/443214/3.zip", 23132,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                4, 1, "987", "443214", "https://example.org/987/443214/4.zip", 23144,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
            DiagnosisKeysFileModel(
                5, 1, "987", "443214", "https://example.org/987/443214/5.zip", 23177,
                state = DiagnosisKeysFileModel.State.None.value,
                isListed = true
            ),
        )

        val mockPathProvider =
            mock<PathSource> {
                on { diagnosisKeysFileDir() } doReturn tmpFolder.root
            }
        val mockDateTimeSource =
            mock<DateTimeSource> {
            }
        val mockDiagnosisKeysFileDao =
            mock<DiagnosisKeysFileDao> {
                onBlocking {
                    findAllBy(
                        any(),
                        anyOrNull()
                    )
                } doReturn existDiagnosisKeysFileList
                onBlocking {
                    findNotCompleted(
                        any(),
                        anyOrNull()
                    )
                } doReturn expectedDiagnosisKeysFileList
            }
        val mockDiagnosisKeyListProvideServiceApi =
            mock<DiagnosisKeyListApi> {
                onBlocking { getList(any()) } doReturn emptyList()
                onBlocking { getList(any(), any()) } doReturn listedDiagnosisKeysFileList
            }
        val mockDiagnosisKeyFileProvideServiceApi =
            mock<DiagnosisKeyFileApi> {
            }

        val repository = DiagnosisKeysFileRepositoryImpl(
            mockContext,
            mockPathProvider,
            mockDateTimeSource,
            mockDiagnosisKeysFileDao,
            mockDiagnosisKeyListProvideServiceApi,
            mockDiagnosisKeyFileProvideServiceApi,
        )

        val actualResult = repository
            .getDiagnosisKeysFileList("987", "443214")

        Assert.assertEquals(expectedDiagnosisKeysFileList, actualResult)

        verify(mockDiagnosisKeyListProvideServiceApi, never()).getList(eq("987"))
        verify(mockDiagnosisKeyListProvideServiceApi, times(1)).getList(eq("987"), eq("443214"))

        verify(mockDiagnosisKeysFileDao, times(1)).findAllBy(
            eq("987"),
            eq("443214")
        )
        verify(
            mockDiagnosisKeysFileDao,
            times(1)
        ).findNotCompleted(eq("987"), eq("443214"))
        verify(mockDiagnosisKeysFileDao, times(1)).insertAll(
            listOf(
                DiagnosisKeysFileModel(
                    0, 0, "987", "443214", "https://example.org/987/443214/5.zip", 23177,
                    state = DiagnosisKeysFileModel.State.None.value,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).updateAll(
            listOf(
                DiagnosisKeysFileModel(
                    2, 1, "987", "443214", "https://example.org/987/443214/2.zip", 23128,
                    state = DiagnosisKeysFileModel.State.Completed.value,
                    isListed = true
                ),
                DiagnosisKeysFileModel(
                    3, 1, "987", "443214", "https://example.org/987/443214/3.zip", 23132,
                    state = DiagnosisKeysFileModel.State.None.value,
                    isListed = true
                ),
                DiagnosisKeysFileModel(
                    4, 1, "987", "443214", "https://example.org/987/443214/4.zip", 23144,
                    state = DiagnosisKeysFileModel.State.None.value,
                    isListed = true
                ),
            )
        )
        verify(mockDiagnosisKeysFileDao, times(1)).deleteAll(
            listOf(
                DiagnosisKeysFileModel(
                    1, 1, "987", "443214", "https://example.org/987/443214/1.zip", 23123,
                    state = DiagnosisKeysFileModel.State.Completed.value,
                    isListed = false
                ),
            )
        )
    }
}
