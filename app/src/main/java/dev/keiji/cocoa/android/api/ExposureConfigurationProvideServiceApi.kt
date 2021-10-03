package dev.keiji.cocoa.android.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.entity.ExposureConfiguration
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Singleton

interface ExposureConfigurationProvideServiceApi {
    @GET("exposure_configuration.json")
    suspend fun getConfiguration(
    ): ExposureConfiguration?

    @GET("{slot}/exposure_configuration.json")
    suspend fun getConfiguration(
        @Path("slot") slot: String,
    ): ExposureConfiguration?
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureConfigurationProvideServiceApiModule {

    @Singleton
    @Provides
    fun provideExposureConfigurationProvideServiceApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): ExposureConfigurationProvideServiceApi {
        val contentType = MediaType.parse("application/json")!!

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.EXPOSURE_CONFIGURATION_API_ENDPOINT)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(ExposureConfigurationProvideServiceApi::class.java)
    }
}
