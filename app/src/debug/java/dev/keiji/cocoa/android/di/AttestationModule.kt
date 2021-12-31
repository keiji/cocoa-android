package dev.keiji.cocoa.android.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.AttestationApiMock
import dev.keiji.cocoa.android.common.attestation.AttestationApi

@Module
@InstallIn(SingletonComponent::class)
object AttestationModule {

    @Provides
    fun provideAttestationApi(
    ): AttestationApi {
        return AttestationApiMock()
    }
}
