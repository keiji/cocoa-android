package dev.keiji.cocoa.android.exposure_notificaiton.repository

import android.content.Context
import android.content.SharedPreferences
import dev.keiji.cocoa.android.common.provider.DateTimeProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner.Silent::class)
class UserDataRepositoryTest {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun setProcessedDiagnosisKeyFileTimestampTest_region() {
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockSharedPreferencesEditor =
            mock<SharedPreferences.Editor> {
            }
        val mockSharedPreferences =
            mock<SharedPreferences> {
                on { edit() } doReturn mockSharedPreferencesEditor
            }

        val repository = UserDataRepositoryImpl(
            mockContext,
            mockDateTimeProvider,
            mockSharedPreferences,
        )

        repository
            .setProcessedDiagnosisKeyFileTimestamp("region1", null, 123456L)

        verify(mockSharedPreferences).edit()
        verify(mockSharedPreferencesEditor)
            .putString(
                UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                "{\"region1\":123456}"
            )
        verify(mockSharedPreferencesEditor).commit()
    }

    @Test
    fun setProcessedDiagnosisKeyFileTimestampTest_region_subregion() {
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockSharedPreferencesEditor =
            mock<SharedPreferences.Editor> {
            }
        val mockSharedPreferences =
            mock<SharedPreferences> {
                on { edit() } doReturn mockSharedPreferencesEditor
            }

        val repository = UserDataRepositoryImpl(
            mockContext,
            mockDateTimeProvider,
            mockSharedPreferences,
        )

        repository
            .setProcessedDiagnosisKeyFileTimestamp("region1", "subregion6", 123458L)

        verify(mockSharedPreferences).edit()
        verify(mockSharedPreferencesEditor)
            .putString(
                UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                "{\"region1-subregion6\":123458}"
            )
        verify(mockSharedPreferencesEditor).commit()
    }

    @Test
    fun getProcessedDiagnosisKeyFileTimestampTest_notfound1() {
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockSharedPreferences =
            mock<SharedPreferences> {
                on {
                    getString(
                        UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                        null
                    )
                } doReturn null
            }

        val repository = UserDataRepositoryImpl(
            mockContext,
            mockDateTimeProvider,
            mockSharedPreferences,
        )

        val timestamp = repository
            .getProcessedDiagnosisKeyFileTimestamp("region1", null)

        verify(mockSharedPreferences)
            .getString(
                UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                null
            )

        Assert.assertEquals(0L, timestamp)
    }

    @Test
    fun getProcessedDiagnosisKeyFileTimestampTest_notfound2() {
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockSharedPreferences =
            mock<SharedPreferences> {
                on {
                    getString(
                        UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                        null
                    )
                } doReturn "{\"region1-subregion6\":123458}"
            }

        val repository = UserDataRepositoryImpl(
            mockContext,
            mockDateTimeProvider,
            mockSharedPreferences,
        )

        val timestamp = repository
            .getProcessedDiagnosisKeyFileTimestamp("region1", null)

        verify(mockSharedPreferences)
            .getString(
                UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                null
            )

        Assert.assertEquals(0L, timestamp)
    }

    @Test
    fun getProcessedDiagnosisKeyFileTimestampTest_found1() {
        val mockDateTimeProvider =
            mock<DateTimeProvider> {
            }
        val mockSharedPreferences =
            mock<SharedPreferences> {
                on {
                    getString(
                        UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                        null
                    )
                } doReturn "{\"region1-subregion6\":123458}"
            }

        val repository = UserDataRepositoryImpl(
            mockContext,
            mockDateTimeProvider,
            mockSharedPreferences,
        )

        val timestamp = repository
            .getProcessedDiagnosisKeyFileTimestamp("region1", "subregion6")

        verify(mockSharedPreferences)
            .getString(
                UserDataRepositoryImpl.PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
                null
            )

        Assert.assertEquals(123458L, timestamp)
    }
}