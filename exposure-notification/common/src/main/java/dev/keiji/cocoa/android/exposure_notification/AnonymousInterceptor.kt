package dev.keiji.cocoa.android.exposure_notification

import okhttp3.Interceptor
import okhttp3.Response

class AnonymousInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val anonymousRequest = chain.request().newBuilder()
            .header("User-Agent", "")
            .build()
        return chain.proceed(anonymousRequest)
    }
}
