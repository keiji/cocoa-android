package dev.keiji.cocoa.android.api

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.entity.ExposureConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl

import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Singleton

class ExposureConfigurationProvideServiceApi(
    private val okHttpClient: OkHttpClient,
    private val fileDir: File,
) {
    private val DIR_NAME = "configuration"

    suspend fun getConfiguration(): ExposureConfiguration? = withContext(Dispatchers.Main) {
        val outputDir = File(fileDir, DIR_NAME)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val url = HttpUrl.parse(BuildConfig.EXPOSURE_CONFIGURATION_URL)
        if (url == null) {
            Timber.i("url is null. ${BuildConfig.EXPOSURE_CONFIGURATION_URL}")
            return@withContext null
        }

        val outputFile = File(outputDir, url.encodedPath().split("/").last())
        Timber.d("outputFile path ${outputFile.absolutePath}")

        if (outputFile.exists()) {
            return@withContext loadExposureConfiguration(outputFile)
        }

        val request = Request.Builder()
            .url(url)
            .build()
        val call = okHttpClient.newCall(request)

        launch(Dispatchers.IO) {
            val response = call.execute()

            response.body()?.byteStream()?.use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        return@withContext loadExposureConfiguration(outputFile)
    }

    suspend fun loadExposureConfiguration(outputFile: File) = withContext(Dispatchers.IO) {
        return@withContext FileInputStream(outputFile).bufferedReader().use { reader ->
            Json.decodeFromString<ExposureConfiguration>(reader.readText())
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureConfigurationProvideServiceApiModule {

    @Singleton
    @Provides
    fun provideExposureConfigurationProvideServiceApi(
        @ApplicationContext applicationContext: Context,
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): ExposureConfigurationProvideServiceApi {
        return ExposureConfigurationProvideServiceApi(
            okHttpClient,
            applicationContext.filesDir,
        )
    }
}
