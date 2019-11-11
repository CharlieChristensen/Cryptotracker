package com.charliechristensen.remote.di

import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import com.charliechristensen.remote.websocket.WebSocketServiceImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@FlowPreview
@Suppress("unused")
@Module
object RemoteModule {

    @Provides
    @Singleton
    fun provideApiService(
        @Named("BaseUrl") baseUrl: String
    ): CryptoService {
        val retrofit = provideRetrofit(baseUrl)
        return retrofit.create(CryptoService::class.java)
    }

    @Provides
    @Singleton
    fun provideWebSocket(
        @Named("WebSocketUrl") webSocketUrl: String
    ): WebSocketService = WebSocketServiceImpl(webSocketUrl)

    private fun provideRetrofit(
        baseUrl: String
    ): Retrofit {
        val moshi = provideMoshi()
        val okHttpClient = provideOkHttpClient()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .callFactory { okHttpClient.get().newCall(it) }
            .addConverterFactory(provideMoshiConverterFactory(moshi))
            .build()
    }

    private fun provideOkHttpClient(): dagger.Lazy<OkHttpClient> = dagger.Lazy { OkHttpClient() }

    private fun provideMoshi(): Moshi = Moshi.Builder().build()

    private fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(
            moshi
        )

}
