package dev.keiji.cocoa.android.exposure_notification.exposure_detection.api

import dev.keiji.cocoa.android.exposure_notification.model.DiagnosisKeysFileModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

interface DiagnosisKeyFileApi {
    suspend fun downloadFile(diagnosisKeysFileModel: DiagnosisKeysFileModel, outputDir: File): File?
}

class DiagnosisKeyFileApiImpl(
    private val okHttpClient: OkHttpClient
) : DiagnosisKeyFileApi {

    override suspend fun downloadFile(
        diagnosisKeysFileModel: DiagnosisKeysFileModel,
        outputDir: File
    ): File? =
        withContext(Dispatchers.Main) {
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val url = HttpUrl.parse(diagnosisKeysFileModel.url)
            if (url == null) {
                Timber.w("DiagnosisKeysEntry.url is null")
                return@withContext null
            }

            val outputFile = File(outputDir, url.encodedPath().split("/").last())
            Timber.d("OutputFile path ${outputFile.absolutePath}")

            val request = Request.Builder()
                .url(url)
                .build()
            val call = okHttpClient.newCall(request)

            withContext(Dispatchers.IO) {
                val response = call.execute()

                if (response.isSuccessful) {
                    response.body()?.byteStream()?.use { inputStream ->
                        FileOutputStream(outputFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    Timber.d("Download completed. From:${diagnosisKeysFileModel.url} To:${outputFile.absolutePath}")
                } else {
                    Timber.d("Download failed. From:${diagnosisKeysFileModel.url} StatusCode:${response.code()}")
                }
            }

            return@withContext outputFile
        }
}
