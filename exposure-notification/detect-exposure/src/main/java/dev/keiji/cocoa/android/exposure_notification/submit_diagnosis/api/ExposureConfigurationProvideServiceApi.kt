package dev.keiji.cocoa.android.exposure_notification.submit_diagnosis.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

interface ExposureConfigurationProvideServiceApi {
    suspend fun getConfiguration(url: HttpUrl, outputFile: File): File
}

class ExposureConfigurationProvideServiceApiImpl(
    private val okHttpClient: OkHttpClient,
) : ExposureConfigurationProvideServiceApi {
    override suspend fun getConfiguration(url: HttpUrl, outputFile: File) = withContext(Dispatchers.Main) {
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
        return@withContext outputFile
    }
}
