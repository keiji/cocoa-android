package dev.keiji.cocoa.android

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.exposure_notiffication.ExposureNotificationWrapper

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
