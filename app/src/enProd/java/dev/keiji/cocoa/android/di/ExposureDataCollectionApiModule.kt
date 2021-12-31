package dev.keiji.cocoa.android.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExposureDataCollectionApiModule {

    @Singleton
    @Provides
    fun provideExposureDataCollectionApi(
        @AnonymousInterceptorOkHttpClient okHttpClient: OkHttpClient,
        configurationSource: ConfigurationSource,
    ): ExposureDataCollectionApi {
        val contentType = MediaType.parse("application/json")!!

        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            useArrayPolymorphism = true
            coerceInputValues = false
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(configurationSource.exposureDataCollectionApiEndpoint)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ExposureDataCollectionApi::class.java)
    }
}
