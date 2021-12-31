package dev.keiji.cocoa.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.common.attestation.AttestationApi
import dev.keiji.cocoa.android.common.attestation.AttestationApiImpl

@Module
@InstallIn(SingletonComponent::class)
object AttestationModule {

    @Provides
    fun provideAttestationApi(
        @ApplicationContext applicationContext: Context,
    ): AttestationApi {
        return AttestationApiImpl(
            applicationContext,
            BuildConfig.ATTESTATION_API_KEY
        )
    }
}
