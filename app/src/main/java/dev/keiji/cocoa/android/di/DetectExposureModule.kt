package dev.keiji.cocoa.android.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyFileProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyListProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureConfigurationProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureConfigurationProvideServiceApiImpl
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionServiceApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.DiagnosisKeysFileRepository
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.DiagnosisKeysFileRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.source.DatabaseSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyFileProvideServiceApiImpl
import dev.keiji.cocoa.android.source.DateTimeSource
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

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

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisKeyListProvideServiceApiModule {

    @Singleton
    @Provides
    fun provideDiagnosisKeyListProvideServiceApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): DiagnosisKeyListProvideServiceApi {
        val contentType = MediaType.parse("application/json")!!

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.DIAGNOSIS_KEY_API_ENDPOINT)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(DiagnosisKeyListProvideServiceApi::class.java)
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
        return ExposureConfigurationProvideServiceApiImpl(
            okHttpClient,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisKeysFileRepositoryModule {

    @Singleton
    @Provides
    fun provideDiagnosisKeysFileRepository(
        @ApplicationContext applicationContext: Context,
        pathSource: PathSource,
        dateTimeSource: DateTimeSource,
        databaseSource: DatabaseSource,
        diagnosisKeyListProvideServiceApi: DiagnosisKeyListProvideServiceApi,
        diagnosisKeyFileProvideServiceApi: DiagnosisKeyFileProvideServiceApi,

        ): DiagnosisKeysFileRepository {
        return DiagnosisKeysFileRepositoryImpl(
            applicationContext,
            pathSource,
            dateTimeSource,
            databaseSource.dbInstance().diagnosisKeyFileDao(),
            diagnosisKeyListProvideServiceApi,
            diagnosisKeyFileProvideServiceApi
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureConfigurationRepositoryModule {

    @Singleton
    @Provides
    fun provideExposureConfigurationRepository(
        @ApplicationContext applicationContext: Context,
        pathSource: PathSource,
        configurationSource: ConfigurationSource,
        exposureConfigurationProvideServiceApi: ExposureConfigurationProvideServiceApi,
    ): ExposureConfigurationRepository {
        return ExposureConfigurationRepositoryImpl(
            applicationContext,
            pathSource,
            exposureConfigurationProvideServiceApi,
            configurationSource,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureDataCollectionServiceApiModule {

    @Singleton
    @Provides
    fun provideExposureDataCollectionServiceApi(
        @AnonymousInterceptorOkHttpClient okHttpClient: OkHttpClient,
        configurationSource: ConfigurationSource,
        ): ExposureDataCollectionServiceApi {
        val contentType = MediaType.parse("application/json")!!

        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            useArrayPolymorphism = true
            coerceInputValues = false
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(configurationSource.exposureDataCollectionApiEndpoint())
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ExposureDataCollectionServiceApi::class.java)
    }
}
