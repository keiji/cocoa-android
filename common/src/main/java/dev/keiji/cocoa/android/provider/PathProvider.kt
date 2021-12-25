package dev.keiji.cocoa.android.provider

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

interface PathProvider {
    fun exposureConfigurationDir(): File
}

class PathProviderImpl(
    private val applicationContext: Context,
) : PathProvider {
    companion object {
        private const val DIR_NAME = "configuration"
    }

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
