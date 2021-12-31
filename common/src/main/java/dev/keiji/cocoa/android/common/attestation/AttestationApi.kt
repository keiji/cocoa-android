package dev.keiji.cocoa.android.common.attestation

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.security.MessageDigest

interface AttestationApi {
    suspend fun attest(attestationRequest: AttestationRequest): String
}

class AttestationApiImpl(
    private val applicationContext: Context,
    private val apiKey: String,
) : AttestationApi {

    private val sha256 = MessageDigest.getInstance("SHA-256")

    override suspend fun attest(attestationRequest: AttestationRequest): String {
        try {
            val clearText = attestationRequest.getClearText()
            val nonce = sha256.digest(clearText.encodeToByteArray())

            val response = SafetyNet.getClient(applicationContext).attest(nonce, apiKey).await()
            return response.jwsResult ?: throw AttestationException(
                "Response.jwsResult is null",
                AttestationException.STATUS_CODE_INVALID_RESPONSE
            )
        } catch (exception: ApiException) {
            throw AttestationException(exception.message, exception.statusCode)
        } catch (exception: Exception) {
            throw exception
        }
    }
}

class AttestationException(
    message: String?,
    val statusCode: Int
) : Exception(message) {
    companion object {
        const val STATUS_CODE_INVALID_RESPONSE = -1
    }

}
