package dev.keiji.cocoa.android.exposure_notification.source

import android.content.Context
import androidx.room.Room
import dev.keiji.cocoa.android.exposure_notification.AppDatabase

interface DatabaseSource {
    fun dbInstance(): AppDatabase
}

class DatabaseSourceImpl(
    private val applicationContext: Context,
) : DatabaseSource {

    companion object {
        private const val FILENAME_DATABASE = "database"
    }

    private var instance: AppDatabase? = null

    override fun dbInstance(): AppDatabase {
        synchronized(this) {
            val instanceSnapshot = instance

            if (instanceSnapshot != null) {
                return instanceSnapshot
            }

            return Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                FILENAME_DATABASE
            ).build().also {
                instance = it
            }
        }
    }
}
