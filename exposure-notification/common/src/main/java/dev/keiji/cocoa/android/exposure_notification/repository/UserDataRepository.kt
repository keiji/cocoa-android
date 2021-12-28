package dev.keiji.cocoa.android.exposure_notification.repository

import android.content.Context
import android.content.SharedPreferences
import dev.keiji.cocoa.android.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.dao.DiagnosisKeysFileDao

interface UserDataRepository {
}

class UserDataRepositoryImpl(
    applicationContext: Context,
    private val dateTimeSource: DateTimeSource,
    private val preferences: SharedPreferences,
    private val diagnosisKeysFileDao: DiagnosisKeysFileDao
) : UserDataRepository {
}
