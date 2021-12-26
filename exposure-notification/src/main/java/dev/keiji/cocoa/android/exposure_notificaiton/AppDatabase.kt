package dev.keiji.cocoa.android.exposure_notificaiton

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.keiji.cocoa.android.exposure_notificaiton.dao.DiagnosisKeysFileDao
import dev.keiji.cocoa.android.exposure_notificaiton.entity.DiagnosisKeysFile

@Database(entities = arrayOf(DiagnosisKeysFile::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diagnosisKeyFileDao(): DiagnosisKeysFileDao
}