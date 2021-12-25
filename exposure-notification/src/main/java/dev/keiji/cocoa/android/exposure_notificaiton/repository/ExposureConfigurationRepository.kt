package dev.keiji.cocoa.android.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.common.provider.PathProvider
import dev.keiji.cocoa.android.exposure_notificaiton.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.api.ExposureConfigurationProvideServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import java.io.File
import javax.inject.Singleton

interface ExposureConfigurationRepository {
    suspend fun getExposureConfiguration(url: String): ExposureConfiguration
}

class ExposureConfigurationRepositoryImpl(
    private val applicationContext: Context,
    private val pathProvider: PathProvider,
    private val exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi
) : ExposureConfigurationRepository {
    companion object {
        private const val FILENAME = "exposure_configuration.json"
    }

    override suspend fun getExposureConfiguration(url: String): ExposureConfiguration =
        withContext(Dispatchers.Main) {
            val outputDir = pathProvider.exposureConfigurationDir()
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val httpUrl = HttpUrl.parse(url)
            val outputFile = File(outputDir, FILENAME)

            if (httpUrl != null && !outputFile.exists()) {
                exposureConfigurationProvideServiceApi.getConfiguration(
                    httpUrl,
                    outputFile
                )
            }

            return@withContext if (outputFile.exists()) {
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
        pathProvider: PathProvider,
        exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi,
    ): ExposureConfigurationRepository {
        return ExposureConfigurationRepositoryImpl(
            applicationContext,
            pathProvider,
            exposureConfigurationProvideServiceApi
        )
    }
}
