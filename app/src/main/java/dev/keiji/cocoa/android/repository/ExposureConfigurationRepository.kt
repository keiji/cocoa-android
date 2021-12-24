package dev.keiji.cocoa.android.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.api.ExposureConfigurationProvideServiceApi
import dev.keiji.cocoa.android.entity.ExposureConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import java.io.File
import javax.inject.Singleton

interface ExposureConfigurationRepository {
    suspend fun getExposureConfiguration(): ExposureConfiguration
}

class ExposureConfigurationRepositoryImpl(
    private val applicationContext: Context,
    private val exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi
) : ExposureConfigurationRepository {
    companion object {
        private const val DIR_NAME = "configuration"
        private const val FILENAME = "exposure_configuration.json"
    }

    override suspend fun getExposureConfiguration(): ExposureConfiguration {
        val outputDir = File(applicationContext.filesDir, DIR_NAME)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val outputFile = File(outputDir, FILENAME)
        val url = HttpUrl.parse(BuildConfig.EXPOSURE_CONFIGURATION_URL)!!

        if (!outputFile.exists()) {
            exposureConfigurationProvideServiceApi.getConfiguration(url, outputFile)
        }

        return if (outputFile.exists()) {
            withContext(Dispatchers.IO) {
                Json.decodeFromString<ExposureConfiguration>(outputFile.readText()).apply {
                    appleExposureConfigV1 = null
                    appleExposureConfigV2 = null
                }
            }
        } else {
            ExposureConfiguration()
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
        exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi,
    ): ExposureConfigurationRepository {
        return ExposureConfigurationRepositoryImpl(
            applicationContext,
            exposureConfigurationProvideServiceApi
        )
    }
}
