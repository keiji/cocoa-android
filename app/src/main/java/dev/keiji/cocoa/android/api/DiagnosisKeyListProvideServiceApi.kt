package dev.keiji.cocoa.android.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.keiji.cocoa.android.BuildConfig
import dev.keiji.cocoa.android.DefaultInterceptorOkHttpClient
import dev.keiji.cocoa.android.entity.DiagnosisKeysEntry
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Path
import retrofit2.http.GET

import javax.inject.Singleton

interface DiagnosisKeyListProvideServiceApi {
    @GET("diagnosis_keys/{region}/list.json")
    suspend fun getList(
        @Path("region") region: String,
    ): List<DiagnosisKeysEntry?>

    @GET("diagnosis_keys/{region}/{subregion}/list.json")
    suspend fun getList(
        @Path("region") region: String,
        @Path("subregion") subregion: String,
    ): List<DiagnosisKeysEntry?>
}

@Module
@InstallIn(SingletonComponent::class)
object DiagnosisKeyListProvideServiceApiModule {

    @Singleton
    @Provides
    fun provideDiagnosisKeyListProvideServiceApi(
        @DefaultInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): DiagnosisKeyListProvideServiceApi {
        val contentType = MediaType.parse("application/json")!!

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.DIAGNOSIS_KEY_API_ENDPOINT)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
            .create(DiagnosisKeyListProvideServiceApi::class.java)
    }
}
