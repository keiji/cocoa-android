package dev.keiji.cocoa.android.exposure_notification

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.keiji.cocoa.android.exposure_notification.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notification.entity.DiagnosisKeysFile

@Database(entities = [DiagnosisKeysFile::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosisKeyFileDao(): DiagnosisKeysFileDao
}