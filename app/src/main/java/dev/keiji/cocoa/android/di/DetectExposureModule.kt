package dev.keiji.cocoa.android.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.exposure_notification.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionService
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.ExposureDetectionServiceImpl
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyFileApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyListApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureConfigurationApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureConfigurationApiImpl
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.ExposureDataCollectionApi
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.DiagnosisKeysFileRepository
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.DiagnosisKeysFileRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.repository.ExposureConfigurationRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.source.DatabaseSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.exposure_detection.api.DiagnosisKeyFileApiImpl
import dev.keiji.cocoa.android.common.source.DateTimeSource
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.dao.DailySummaryDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureDataDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureInformationDao
import dev.keiji.cocoa.android.exposure_notification.dao.ExposureWindowDao
import dev.keiji.cocoa.android.exposure_notification.repository.ExposureDataRepository
import dev.keiji.cocoa.android.exposure_notification.repository.ExposureDataRepositoryImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ExposureDataRepositoryModule {

    @Singleton
    @Provides
    fun provideExposureDataRepository(
        databaseSource: DatabaseSource,
        dateTimeSource: DateTimeSource,
    ): ExposureDataRepository {
        val db = databaseSource.dbInstance()
        return ExposureDataRepositoryImpl(
            dateTimeSource,
            db.exposureDataDao(),
            db.exposureInformationDao(),
            db.dailySummaryDao(),
            db.exposureWindowDao(),
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureDetectionServiceModule {

    @Singleton
    @Provides
    fun provideDiagnosisKeyFileProvideServiceApi(
        dateTimeSource: DateTimeSource,
        exposureConfigurationRepository: ExposureConfigurationRepository,
        exposureDataRepository: ExposureDataRepository,
        exposureDataCollectionApi: ExposureDataCollectionApi,
        diagnosisKeysFileRepository: DiagnosisKeysFileRepository,
        configurationSource: ConfigurationSource,
        exposureNotificationWrapper: ExposureNotificationWrapper,
    ): ExposureDetectionService {
        return ExposureDetectionServiceImpl(
            dateTimeSource,
            exposureConfigurationRepository,
            exposureDataRepository,
            exposureDataCollectionApi,
            diagnosisKeysFileRepository,
            configurationSource,
            exposureNotificationWrapper,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisKeyFileProvideApiModule {

    @Singleton
    @Provides
    fun provideDiagnosisKeyFileProvideApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): DiagnosisKeyFileApi {
        return DiagnosisKeyFileApiImpl(
            okHttpClient
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisKeyListProvideApiModule {

    private val json = Json { ignoreUnknownKeys = true }

    @Singleton
    @Provides
    fun provideDiagnosisKeyListProvideApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): DiagnosisKeyListApi {
        val contentType = MediaType.parse("application/json")!!

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.DIAGNOSIS_KEY_API_ENDPOINT)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(DiagnosisKeyListApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ExposureConfigurationProvideApiModule {

    @Singleton
    @Provides
    fun provideExposureConfigurationProvideApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): ExposureConfigurationApi {
        return ExposureConfigurationApiImpl(
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
        diagnosisKeyListApi: DiagnosisKeyListApi,
        diagnosisKeyFileApi: DiagnosisKeyFileApi,

        ): DiagnosisKeysFileRepository {
        return DiagnosisKeysFileRepositoryImpl(
            applicationContext,
            pathSource,
            dateTimeSource,
            databaseSource.dbInstance().diagnosisKeysFileDao(),
            diagnosisKeyListApi,
            diagnosisKeyFileApi
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
        exposureConfigurationApi: ExposureConfigurationApi,
    ): ExposureConfigurationRepository {
        return ExposureConfigurationRepositoryImpl(
            applicationContext,
            pathSource,
            exposureConfigurationApi,
            configurationSource,
        )
    }
}
