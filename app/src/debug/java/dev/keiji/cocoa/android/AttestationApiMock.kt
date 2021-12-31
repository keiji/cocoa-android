package dev.keiji.cocoa.android

import dev.keiji.cocoa.android.common.attestation.AttestationApi
import dev.keiji.cocoa.android.common.attestation.AttestationRequest

class AttestationApiMock : AttestationApi {
    override suspend fun attest(attestationRequest: AttestationRequest) = ""
}
