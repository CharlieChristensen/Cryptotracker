package com.charliechristensen.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request()
                .newBuilder()
                .header(
                    "authorization",
                    "Apikey $apiKey"
                )
                .build()
        )
    }
}
