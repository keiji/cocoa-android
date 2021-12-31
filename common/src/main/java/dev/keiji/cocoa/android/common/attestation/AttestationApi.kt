package dev.keiji.cocoa.android.common.attestation

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNet
import kotlinx.coroutines.tasks.await
import java.lang.Exception

interface AttestationApi {
    suspend fun attest(nonce: ByteArray): String?
}

class AttestationApiImpl(
    private val applicationContext: Context,
    private val apiKey: String,
) : AttestationApi {

    override suspend fun attest(nonce: ByteArray): String? {
        try {
            val response = SafetyNet.getClient(applicationContext).attest(nonce, apiKey).await()
            return response.jwsResult
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
) : Exception(message)
