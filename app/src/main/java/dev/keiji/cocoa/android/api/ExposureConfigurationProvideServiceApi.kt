package dev.keiji.cocoa.android.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.DefaultInterceptorOkHttpClient
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

class ExposureConfigurationProvideServiceApi(
    private val okHttpClient: OkHttpClient,
) {
    suspend fun getConfiguration(outputFile: File) = withContext(Dispatchers.Main) {
        val url = HttpUrl.parse(BuildConfig.EXPOSURE_CONFIGURATION_URL)
        if (url == null) {
            Timber.i("url is null. ${BuildConfig.EXPOSURE_CONFIGURATION_URL}")
            return@withContext
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
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureConfigurationProvideServiceApiModule {

    @Singleton
    @Provides
    fun provideExposureConfigurationProvideServiceApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): ExposureConfigurationProvideServiceApi {
        return ExposureConfigurationProvideServiceApi(
            okHttpClient,
        )
    }
}
