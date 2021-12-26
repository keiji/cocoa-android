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
}

class UserDataRepositoryImpl(
    applicationContext: Context,
    private val dateTimeProvider: DateTimeProvider,
    private val preferences: SharedPreferences,
    private val diagnosisKeysFileDao: DiagnosisKeysFileDao
) : UserDataRepository {
}

@Module
@InstallIn(SingletonComponent::class)
object UserDataRepositoryModule {
    private const val PREFERENCE_NAME = "pref.xml"

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
            applicationContext.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE),
            databaseProvider.dbInstance().diagnosisKeyFileDao()
        )
    }
}
