package dev.keiji.cocoa.android.exposure_notification.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notificaiton.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notificaiton.entity.DiagnosisKeysEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Singleton

class DiagnosisKeyFileProvideServiceApi(
    private val okHttpClient: OkHttpClient
) {
    suspend fun getFile(diagnosisKeysEntry: DiagnosisKeysEntry, outputDir: File): File? =
        withContext(Dispatchers.Main) {
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val url = HttpUrl.parse(diagnosisKeysEntry.url)
            if (url == null) {
                Timber.w("DiagnosisKeysEntry.url is null")
                return@withContext null
            }

            val outputFile = File(outputDir, url.encodedPath().split("/").last())
            Timber.d("outputFile path ${outputFile.absolutePath}")

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
            Timber.d("download completed. From:${diagnosisKeysEntry.url} To:${outputFile.absolutePath}")

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
        return DiagnosisKeyFileProvideServiceApi(
            okHttpClient
        )
    }
}
