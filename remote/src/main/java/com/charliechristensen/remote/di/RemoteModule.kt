package com.charliechristensen.remote.di

import android.app.Application
import com.charliechristensen.remote.RemoteGateway
import com.charliechristensen.remote.RemoteGatewayImpl
import com.charliechristensen.remote.interceptors.ApiKeyInterceptor
import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import com.charliechristensen.remote.websocket.WebSocketServiceImpl
import com.charliechristensen.remote.websocketv2.FlowStreamAdapterFactory
import com.charliechristensen.remote.websocketv2.SocketService
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.retry.ExponentialWithJitterBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton
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
        networkFlipperPlugin: NetworkFlipperPlugin,
        @Named("IsDebug") isDebug: Boolean,
        @InternalApi httpLoggingInterceptor: HttpLoggingInterceptor,
        @InternalApi apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient = OkHttpClient
        .Builder().apply {
            addInterceptor(apiKeyInterceptor)
            if (isDebug) {
                addInterceptor(httpLoggingInterceptor)
                addNetworkInterceptor(FlipperOkhttpInterceptor(networkFlipperPlugin))
            }
        }
        .build()

    @Provides
    @InternalApi
    internal fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Provides
    @InternalApi
    internal fun provideOkHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    @InternalApi
    fun provideScarlet(
        application: Application,
        moshi: Moshi,
        @Named("WebSocketUrlV2") webSocketUrlV2: String,
        @InternalApi okHttpClient: OkHttpClient
    ): Scarlet = Scarlet.Builder()
        .webSocketFactory(okHttpClient.newWebSocketFactory(webSocketUrlV2))
        .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
        .addStreamAdapterFactory(FlowStreamAdapterFactory())
        .backoffStrategy(ExponentialWithJitterBackoffStrategy(5000, 5000))
        .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
        .build()

    @Provides
    @Singleton
    fun provideSocketService(@InternalApi scarlet: Scarlet): SocketService = scarlet.create()

    @Provides
    @InternalApi
    fun providesApiKeyInterceptor(
        @Named("CryptoCompareApiKey") cryptocompareApiKey: String
    ): ApiKeyInterceptor = ApiKeyInterceptor(cryptocompareApiKey)
}
