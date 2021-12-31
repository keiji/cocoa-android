package dev.keiji.cocoa.android

import dev.keiji.cocoa.android.common.attestation.AttestationApi

class AttestationApiMock : AttestationApi {
    override suspend fun attest(nonce: ByteArray): String = ""
}
