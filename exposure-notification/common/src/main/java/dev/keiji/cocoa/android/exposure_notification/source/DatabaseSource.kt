package dev.keiji.cocoa.android.exposure_notification.source

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notification.AppDatabase
import javax.inject.Singleton

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
