package com.charliechristensen.remote.di

import android.app.Application
import com.charliechristensen.remote.RemoteGateway
import com.charliechristensen.remote.RemoteGatewayImpl
import com.charliechristensen.remote.interceptors.ApiKeyInterceptor
import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.webservice.KtorCryptoService
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
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.host
import io.ktor.http.URLProtocol
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton


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
        @Named("WebSocketUrl") webSocketUrl: String,
        @Named("Http") httpClient: HttpClient,
        @Named("WebSocket") webSocketClient: HttpClient
    ): CryptoService = KtorCryptoService(webSocketUrl, httpClient, webSocketClient)

    @Provides
    @InternalApi
    internal fun provideWebSocket(
        webSocketService: WebSocketServiceImpl
    ): WebSocketService = webSocketService

    @Provides
    @Named("Http")
    internal fun provideKtorClient(
        @Named("BaseUrl") baseUrl: String,
        @InternalApi okHttpClient: OkHttpClient,
        @InternalApi serialization: Json
    ): HttpClient = HttpClient(OkHttp) {

        install(JsonFeature) {
            serializer = KotlinxSerializer(serialization)
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("KtorLog").d(message)
                }
            }
        }

        install(WebSockets)

        defaultRequest {
            host = baseUrl
            url {
                protocol = URLProtocol.HTTPS
            }
        }

        engine {
            preconfigured = okHttpClient
        }

    }

    @Provides
    @Named("WebSocket")
    internal fun provideKtorWebSocketClient(
        @Named("WebSocketUrl") baseUrl: String,
        @InternalApi okHttpClient: OkHttpClient,
        @InternalApi serialization: Json
    ): HttpClient = HttpClient(OkHttp) {

//        install(JsonFeature) {
//            serializer = KotlinxSerializer(serialization)
//        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("KtorebSocketLog").d(message)
                }
            }
        }

        install(WebSockets)

        defaultRequest {
            host = baseUrl
            url {
                protocol = URLProtocol.WSS
            }
        }

        engine {
            preconfigured = okHttpClient
        }

    }

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
    internal fun provideSerialization(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

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
        .webSocketFactory(okHttpClient.newWebSocketFactory("https://websockets.com"))
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
