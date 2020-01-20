package com.charliechristensen.remote.di

import com.charliechristensen.remote.RemoteGateway
import com.charliechristensen.remote.RemoteGatewayImpl
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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
private annotation class InternalApi

@Suppress("unused")
@Module
object RemoteModule {

    @Provides
    @Singleton
    fun provideRemoteGateway(
        @InternalApi cryptoService: CryptoService,
        @InternalApi webSocketService: WebSocketService
    ): RemoteGateway = RemoteGatewayImpl(cryptoService, webSocketService)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @InternalApi
    internal fun provideApiService(
        @InternalApi retrofit: Retrofit
    ): CryptoService = retrofit
        .create(CryptoService::class.java)

    @ExperimentalCoroutinesApi
    @Provides
    @InternalApi
    internal fun provideWebSocket(
        webSocketService: WebSocketServiceImpl
    ): WebSocketService = webSocketService

    @Provides
    @InternalApi
    internal fun provideRetrofit(
        @Named("BaseUrl") baseUrl: String,
        @InternalApi okHttpClient: OkHttpClient,
        @InternalApi moshiConverterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(moshiConverterFactory)
        .build()

    @Provides
    @InternalApi
    internal fun provideOkHttpClient(
        @Named("IsDebug") isDebug: Boolean,
        @InternalApi httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient
        .Builder().apply {
            if (isDebug) addInterceptor(httpLoggingInterceptor)
        }
        .build()

    @Provides
    @InternalApi
    internal fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    @InternalApi
    internal fun provideOkHttpLoggingInterceptor(
        @Named("IsDebug") isDebug: Boolean
    ): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
