package dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository

import android.content.Context
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.core.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.ExposureConfigurationProvideServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import java.io.File

interface ExposureConfigurationRepository {
    suspend fun getExposureConfiguration(): ExposureConfiguration
}

class ExposureConfigurationRepositoryImpl(
    private val applicationContext: Context,
    private val pathSource: PathSource,
    private val exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi,
    private val configurationSource: ConfigurationSource,
) : ExposureConfigurationRepository {
    companion object {
        private const val FILENAME = "exposure_configuration.json"
    }

    override suspend fun getExposureConfiguration(): ExposureConfiguration =
        withContext(Dispatchers.IO) {
            val outputDir = pathSource.exposureConfigurationDir()
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val httpUrl = HttpUrl.parse(configurationSource.exposureConfigurationUrl())
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
