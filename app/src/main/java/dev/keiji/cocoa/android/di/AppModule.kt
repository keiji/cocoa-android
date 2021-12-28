package dev.keiji.cocoa.android.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptor
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.BuildConfig
import dev.keiji.cocoa.android.exposure_notification.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.core.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.DiagnosisKeyFileProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.DiagnosisKeyFileProvideServiceApiImpl
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.DiagnosisKeyListProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.ExposureConfigurationProvideServiceApi
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.ExposureConfigurationProvideServiceApiImpl
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.api.ExposureDataCollectionServiceApi
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository.DiagnosisKeysFileRepository
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository.DiagnosisKeysFileRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository.ExposureConfigurationRepository
import dev.keiji.cocoa.android.exposure_notification.detect_exposure.repository.ExposureConfigurationRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.repository.RiskEventRepository
import dev.keiji.cocoa.android.exposure_notification.repository.UserDataRepository
import dev.keiji.cocoa.android.exposure_notification.repository.UserDataRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.source.DatabaseSource
import dev.keiji.cocoa.android.exposure_notification.source.DatabaseSourceImpl
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSourceImpl
import dev.keiji.cocoa.android.source.DateTimeSource
import dev.keiji.cocoa.android.source.DateTimeSourceImpl
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ExposureNotificationModule {

    @Provides
    fun provideExposureNotificationWrapper(
        @ApplicationContext applicationContext: Context
    ): ExposureNotificationWrapper {
        return ExposureNotificationWrapper(applicationContext)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DateTimeProviderModule {

    @Singleton
    @Provides
    fun provideDateTimeProvider(
    ): DateTimeSource = DateTimeSourceImpl()
}

@Module
@InstallIn(SingletonComponent::class)
object UserDataRepositoryModule {
    private const val PREFERENCE_NAME = "pref.xml"

    @Singleton
    @Provides
    fun provideUserDataRepository(
        @ApplicationContext applicationContext: Context,
        dateTimeSource: DateTimeSource,
        databaseSource: DatabaseSource,
    ): UserDataRepository {
        return UserDataRepositoryImpl(
            applicationContext,
            dateTimeSource,
            applicationContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE),
            databaseSource.dbInstance().diagnosisKeyFileDao()
        )
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object RiskEventRepositoryModule {

    @Provides
    fun provideRiskEventRepository(
        @ApplicationContext applicationContext: Context
    ): RiskEventRepository {
        return RiskEventRepository(applicationContext);
    }
}

@Module
@InstallIn(SingletonComponent::class)
object PathProviderModule {

    @Singleton
    @Provides
    fun providePathProvider(
        @ApplicationContext applicationContext: Context,
    ): PathSource {
        return PathSourceImpl(
            applicationContext,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseProviderModule {

    @Singleton
    @Provides
    fun provideDatabaseProvider(
        @ApplicationContext applicationContext: Context,
    ): DatabaseSource {
        return DatabaseSourceImpl(
            applicationContext,
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {

    @DefaultInterceptorOkHttpClient
    @Provides
    fun provideOkHttpClient(
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    fun provideAnonymousInterceptor(
    ): AnonymousInterceptor = AnonymousInterceptor()

    @AnonymousInterceptorOkHttpClient
    @Provides
    fun provideAnonymousInterceptorOkHttpClient(
        anonymousInterceptor: AnonymousInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(anonymousInterceptor)
            .build()
    }
}
