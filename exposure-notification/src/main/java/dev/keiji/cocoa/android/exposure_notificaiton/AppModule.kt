package dev.keiji.cocoa.android.exposure_notificaiton

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AnonymousInterceptorOkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

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
