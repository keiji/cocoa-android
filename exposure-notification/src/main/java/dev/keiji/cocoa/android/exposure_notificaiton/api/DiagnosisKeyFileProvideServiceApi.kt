package dev.keiji.cocoa.android.exposure_notification.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notificaiton.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notificaiton.entity.DiagnosisKeysFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Singleton

interface DiagnosisKeyFileProvideServiceApi {
    suspend fun downloadFile(diagnosisKeysFile: DiagnosisKeysFile, outputDir: File): File?
}

class DiagnosisKeyFileProvideServiceApiImpl(
    private val okHttpClient: OkHttpClient
) : DiagnosisKeyFileProvideServiceApi {

    override suspend fun downloadFile(
        diagnosisKeysFile: DiagnosisKeysFile,
        outputDir: File
    ): File? =
        withContext(Dispatchers.Main) {
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val url = HttpUrl.parse(diagnosisKeysFile.url)
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
                    Timber.d("Download completed. From:${diagnosisKeysFile.url} To:${outputFile.absolutePath}")
                } else {
                    Timber.d("Download failed. From:${diagnosisKeysFile.url} StatusCode:${response.code()}")
                }
            }

            return@withContext outputFile
        }
}

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisKeyFileProvideServiceApiModule {

    @Singleton
    @Provides
    fun provideDiagnosisKeyFileProvideServiceApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): DiagnosisKeyFileProvideServiceApi {
        return DiagnosisKeyFileProvideServiceApiImpl(
            okHttpClient
        )
    }
}
