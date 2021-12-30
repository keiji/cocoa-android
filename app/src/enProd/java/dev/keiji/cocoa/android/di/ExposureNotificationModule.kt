package dev.keiji.cocoa.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapper
import dev.keiji.cocoa.android.exposure_notification.cappuccino.ExposureNotificationWrapperImpl

@Module
@InstallIn(SingletonComponent::class)
object ExposureNotificationModule {

    @Provides
    fun provideExposureNotificationWrapper(
        @ApplicationContext applicationContext: Context
    ): ExposureNotificationWrapper {
        return ExposureNotificationWrapperImpl(applicationContext)
    }
}
