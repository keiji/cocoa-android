package dev.keiji.cocoa.android.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

private const val DIR_NAME = "configuration"

interface PathProvider {
    fun exposureConfigurationDir(): File
}

class PathProviderImpl(
    private val applicationContext: Context,
) : PathProvider {

    override fun exposureConfigurationDir() = File(applicationContext.filesDir, DIR_NAME)
}

@Module
@InstallIn(SingletonComponent::class)
object PathProviderModule {

    @Singleton
    @Provides
    fun providePathProvider(
        @ApplicationContext applicationContext: Context,
    ): PathProvider {
        return PathProviderImpl(
            applicationContext,
        )
    }
}
