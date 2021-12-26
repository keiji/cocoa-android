package dev.keiji.cocoa.android.exposure_notificaiton.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.common.provider.DateTimeProvider
import dev.keiji.cocoa.android.exposure_notificaiton.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notificaiton.provider.DatabaseProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import kotlin.collections.HashMap

interface UserDataRepository {
    fun getProcessedDiagnosisKeyFileTimestamp(region: String, subregion: String?): Long
    fun setProcessedDiagnosisKeyFileTimestamp(region: String, subregion: String?, timestamp: Long)
}

class UserDataRepositoryImpl(
    applicationContext: Context,
    private val dateTimeProvider: DateTimeProvider,
    private val preferences: SharedPreferences,
) : UserDataRepository {

    companion object {
        internal const val PREFERENCE_NAME = "pref.xml"

        internal const val PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT =
            "processed_diagnosis_keys_file_timestamp_dict"

        private const val BLANK_DICTIONARY = "{}"
        private const val BLANK_LIST = "[]"

        private fun createKey(region: String, subregion: String?): String {
            return if (subregion == null) {
                region
            } else {
                "${region}-${subregion}"
            }
        }
    }

    override fun getProcessedDiagnosisKeyFileTimestamp(region: String, subregion: String?): Long {
        val keyTimestampDictStr = preferences.getString(
            PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
            null
        ) ?: BLANK_DICTIONARY
        val dict = Json.decodeFromString<HashMap<String, Long>>(keyTimestampDictStr)

        val key = createKey(region, subregion)
        return dict[key] ?: 0L
    }

    override fun setProcessedDiagnosisKeyFileTimestamp(
        region: String,
        subregion: String?,
        timestamp: Long
    ) {
        val keyTimestampDictStr = preferences.getString(
            PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
            null
        ) ?: BLANK_DICTIONARY
        val dict = Json.decodeFromString<HashMap<String, Long>>(keyTimestampDictStr)

        val key = createKey(region, subregion)
        dict[key] = timestamp

        val newKeyTimestampDictStr = Json.encodeToString(dict)

        val editor  = preferences.edit()
        editor.putString(
            PREFERENCE_KEY_PROCESSED_DIAGNOSIS_KEY_FILE_TIMESTAMP_DICT,
            newKeyTimestampDictStr
        )
        editor.commit()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UserDataRepositoryModule {

    @Singleton
    @Provides
    fun provideUserDataRepository(
        @ApplicationContext applicationContext: Context,
        dateTimeProvider: DateTimeProvider,
        databaseProvider: DatabaseProvider,
    ): UserDataRepository {
        return UserDataRepositoryImpl(
            applicationContext,
            dateTimeProvider,
            applicationContext.getSharedPreferences(UserDataRepositoryImpl.PREFERENCE_NAME, MODE_PRIVATE,
            )
        )
    }
}
