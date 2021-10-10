package dev.keiji.cocoa.android.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.api.ExposureConfigurationProvideServiceApi
import dev.keiji.cocoa.android.entity.ExposureConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import javax.inject.Singleton

class ExposureConfigurationRepository(
    private val applicationContext: Context,
    private val exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi
) {
    companion object {
        private const val DIR_NAME = "configuration"
        private const val FILENAME = "exposure_configuration.json"
    }

    suspend fun getExposureConfiguration(): ExposureConfiguration {
        val outputDir = File(applicationContext.filesDir, DIR_NAME)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val outputFile = File(outputDir, FILENAME)
        if (!outputFile.exists()) {
            exposureConfigurationProvideServiceApi.getConfiguration(outputFile)
        }

        return withContext(Dispatchers.IO) {
            FileInputStream(outputFile).bufferedReader().use { reader ->
                Json.decodeFromString<ExposureConfiguration>(reader.readText()).apply {
                    appleExposureConfigV1 = null
                    appleExposureConfigV2 = null
                }
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureConfigurationRepositoryModule {

    @Singleton
    @Provides
    fun provideExposureConfigurationRepository(
        @ApplicationContext applicationContext: Context,
        exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi
    ): ExposureConfigurationRepository {
        return ExposureConfigurationRepository(
            applicationContext,
            exposureConfigurationProvideServiceApi
        )
    }
}
