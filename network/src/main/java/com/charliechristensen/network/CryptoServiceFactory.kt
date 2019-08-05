package com.charliechristensen.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object CryptoServiceFactory {

    fun makeCryptoService(baseUrl: String, isDebug: Boolean): CryptoService {
        val loggingInterceptor = makeLoggingInterceptor(isDebug)
        val okHttpClient = makeOkHttpClient(loggingInterceptor)
        val moshi = createMoshi()
        val retrofit = makeRetrofit(baseUrl, okHttpClient, moshi)
        return retrofit.create(CryptoService::class.java)
    }

    private fun createMoshi(): Moshi = Moshi.Builder().build()

    private fun makeRetrofit(baseUrl: String, okHttpClient: OkHttpClient, moshi: Moshi) =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    private fun makeOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
        return logging
    }

}