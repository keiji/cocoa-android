package dev.keiji.cocoa.android.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.BuildConfig
import dev.keiji.cocoa.android.exposure_notification.api.DiagnosisSubmissionServiceApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisSubmissionServiceApiModule {

    @Singleton
    @Provides
    fun provideDiagnosisSubmissionServiceApi(
        @AnonymousInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): DiagnosisSubmissionServiceApi {
        val contentType = MediaType.parse("application/json")!!

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.DIAGNOSIS_SUBMISSION_API_ENDPOINT)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(DiagnosisSubmissionServiceApi::class.java)
    }
}
