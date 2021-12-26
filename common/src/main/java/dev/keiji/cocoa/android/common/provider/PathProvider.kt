package dev.keiji.cocoa.android.common.provider

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
    fun diagnosisKeysFileDir(): File
}

class PathProviderImpl(
    private val applicationContext: Context,
) : PathProvider {
    companion object {
        private const val DIR_NAME_CONFIGURATION = "configuration"
        private const val DIR_NAME_DIAGNOSIS_KEYS = "diagnosis_keys"
    }

    override fun exposureConfigurationDir() =
        File(applicationContext.filesDir, DIR_NAME_CONFIGURATION)

    override fun diagnosisKeysFileDir(): File =
        File(applicationContext.filesDir, DIR_NAME_DIAGNOSIS_KEYS)
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
