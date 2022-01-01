package dev.keiji.cocoa.android.exposure_notification.exposure_detection.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Path
import retrofit2.http.GET

interface DiagnosisKeyListApi {
    @GET("{region}/list.json")
    suspend fun getList(
        @Path("region") region: String,
    ): List<Entry?>

    @GET("{region}/{subregion}/list.json")
    suspend fun getList(
        @Path("region") region: String,
        @Path("subregion") subregion: String,
    ): List<Entry?>

    @Serializable
    data class Entry constructor(
        @SerialName("region") val region: Int,
        @SerialName("url") val url: String,
        @SerialName("created") val created: Long,
    )
}
