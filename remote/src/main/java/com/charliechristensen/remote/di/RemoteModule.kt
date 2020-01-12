package com.charliechristensen.remote.di

import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import com.charliechristensen.remote.websocket.WebSocketServiceImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Suppress("unused")
@Module
object RemoteModule {

    @Provides
    @Singleton
    fun provideApiService(
        @Named("BaseUrl") baseUrl: String
    ): CryptoService = provideRetrofit(baseUrl)
        .create(CryptoService::class.java)

    @ExperimentalCoroutinesApi
    @Provides
    @Singleton
    fun provideWebSocket(
        webSocketService: WebSocketServiceImpl
    ): WebSocketService = webSocketService

    private fun provideRetrofit(
        baseUrl: String
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(provideOkHttpClient())
        .addConverterFactory(provideMoshiConverterFactory(provideMoshi()))
        .build()

    private fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

    private fun provideMoshi(): Moshi = Moshi.Builder().build()

    private fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)
}
