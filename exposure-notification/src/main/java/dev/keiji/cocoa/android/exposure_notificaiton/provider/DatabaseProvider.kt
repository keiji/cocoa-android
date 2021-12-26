package dev.keiji.cocoa.android.exposure_notificaiton.provider

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notificaiton.AppDatabase
import javax.inject.Singleton

interface DatabaseProvider {
    fun dbInstance(): AppDatabase
}

class DatabaseProviderImpl(
    private val applicationContext: Context,
) : DatabaseProvider {

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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseProviderModule {

    @Singleton
    @Provides
    fun provideDatabaseProvider(
        @ApplicationContext applicationContext: Context,
    ): DatabaseProvider {
        return DatabaseProviderImpl(
            applicationContext,
        )
    }
}
