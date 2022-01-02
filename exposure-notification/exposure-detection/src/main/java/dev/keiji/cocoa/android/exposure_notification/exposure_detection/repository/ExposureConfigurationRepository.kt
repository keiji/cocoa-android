package dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository

import android.content.Context
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.entity.ExposureConfiguration
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureConfigurationApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import timber.log.Timber
import java.io.File
import java.io.IOException

interface ExposureConfigurationRepository {

    @Throws(IllegalStateException::class)
    suspend fun getExposureConfiguration(): ExposureConfiguration
}

class ExposureConfigurationRepositoryImpl(
    applicationContext: Context,
    pathSource: PathSource,
    private val exposureConfigurationApi: ExposureConfigurationApi,
    private val configurationSource: ConfigurationSource,
) : ExposureConfigurationRepository {

    private val exposureConfigurationDir = pathSource.exposureConfigurationDir()
    private val exposureConfigurationFile = pathSource.exposureConfigurationFile()

    private val cacheDir = applicationContext.cacheDir

    override suspend fun getExposureConfiguration(): ExposureConfiguration =
        withContext(Dispatchers.IO) {
            exposureConfigurationDir.mkdirs()

            var exposureConfiguration: ExposureConfiguration? = null

            val httpUrl = HttpUrl.parse(configurationSource.exposureConfigurationUrl)

            if (httpUrl != null) {
                var tmpFile =
                    File.createTempFile("exposure_configuration", ".json", cacheDir)

                try {
                    tmpFile = exposureConfigurationApi.downloadConfigurationFile(
                        httpUrl,
                        tmpFile
                    )

                    // Check deserializable
                    exposureConfiguration = loadExposureConfiguration(tmpFile)

                    tmpFile.copyTo(exposureConfigurationFile, overwrite = true)
                    tmpFile.delete()

                } catch (e: SerializationException) {
                    Timber.e(e, "SerializationException")
                } catch (e: IOException) {
                    Timber.e(e, "exposureConfigurationApi.getConfiguration")
                } catch (e: Exception) {
                    Timber.e(e, "exposureConfigurationApi.getConfiguration")
                }
            }

            if (exposureConfiguration == null && exposureConfigurationFile.exists()) {
                Timber.d("exposureConfigurationFile ${exposureConfigurationFile.absolutePath} exists")

                try {
                    exposureConfiguration = loadExposureConfiguration(exposureConfigurationFile)
                } catch (e: SerializationException) {
                    Timber.e(e, "SerializationException")
                } catch (e: IOException) {
                    Timber.e(e, "IOException")
                }
            }

            if (exposureConfiguration == null) {
                throw IllegalStateException("ExposureConfiguration could not be loaded.")
            }

            return@withContext exposureConfiguration
        }

    private fun loadExposureConfiguration(file: File): ExposureConfiguration {
        return Json.decodeFromString<ExposureConfiguration>(file.readText())
            .apply {
                appleExposureConfigV1 = null
                appleExposureConfigV2 = null
            }
    }
}
