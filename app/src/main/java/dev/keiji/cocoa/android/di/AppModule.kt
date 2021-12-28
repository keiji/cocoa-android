package dev.keiji.cocoa.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptor
import dev.keiji.cocoa.android.exposure_notification.AnonymousInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.exposure_notification.repository.RiskEventRepository
import dev.keiji.cocoa.android.exposure_notification.repository.UserDataRepository
import dev.keiji.cocoa.android.exposure_notification.repository.UserDataRepositoryImpl
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSource
import dev.keiji.cocoa.android.exposure_notification.source.ConfigurationSourceImpl
import dev.keiji.cocoa.android.exposure_notification.source.DatabaseSource
import dev.keiji.cocoa.android.exposure_notification.source.DatabaseSourceImpl
import dev.keiji.cocoa.android.exposure_notification.source.PathSource
import dev.keiji.cocoa.android.exposure_notification.source.PathSourceImpl
import dev.keiji.cocoa.android.source.DateTimeSource
import dev.keiji.cocoa.android.source.DateTimeSourceImpl
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ConfigurationSourceModule {

    @Singleton
    @Provides
    fun provideConfigurationSource(): ConfigurationSource = ConfigurationSourceImpl(
        BuildConfig.USE_EXPOSURE_WINDOW_MODE,
        BuildConfig.REGION_IDs,
        BuildConfig.SUBREGION_IDs,
        BuildConfig.SUBMIT_DIAGNOSIS_API_ENDPOINT,
        BuildConfig.DIAGNOSIS_KEY_API_ENDPOINT,
        BuildConfig.EXPOSURE_DATA_COLLECTION_API_ENDPOINT,
        BuildConfig.EXPOSURE_CONFIGURATION_URL
    )
}

@Module
@InstallIn(SingletonComponent::class)
object DateTimeSourceModule {

    @Singleton
    @Provides
    fun provideDateTimeSource(
    ): DateTimeSource = DateTimeSourceImpl()
}

@Module
@InstallIn(SingletonComponent::class)
object PathSourceModule {

    @Singleton
    @Provides
    fun providePathSource(
        @ApplicationContext applicationContext: Context,
    ): PathSource {
        return PathSourceImpl(
            applicationContext,
        )
    }
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
object DatabaseSourceModule {

    @Singleton
    @Provides
    fun provideDatabaseSource(
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
