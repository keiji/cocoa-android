package dev.keiji.cocoa.android.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notification.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.diagnosis_submission.api.ENCalibrationSubmitDiagnosisApi
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SubmitDiagnosisModule {

    @Singleton
    @Provides
    fun provideSubmitDiagnosisServiceApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient,
        configurationSource: ConfigurationSource,
    ): ENCalibrationSubmitDiagnosisApi {
        val contentType = MediaType.parse("application/json")!!

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(configurationSource.submitDiagnosisApiEndpoint())
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(ENCalibrationSubmitDiagnosisApi::class.java)
    }
}
