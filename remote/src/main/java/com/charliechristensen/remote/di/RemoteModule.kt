package com.charliechristensen.remote.di

import com.charliechristensen.remote.RemoteGateway
import com.charliechristensen.remote.RemoteGatewayImpl
import com.charliechristensen.remote.interceptors.ApiKeyInterceptor
import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.webservice.KtorCryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import com.charliechristensen.remote.websocket.WebSocketServiceImpl
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber


val remoteModule = module {

    single<RemoteGateway> { RemoteGatewayImpl(get(), get()) }

    single<CryptoService> { KtorCryptoService(get(named("BaseUrl")), get()) }

    single<WebSocketService> { WebSocketServiceImpl(get(), get()) }

    single {
        HttpClient(OkHttp) {

            install(JsonFeature) {
                serializer = KotlinxSerializer(get())
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.tag("KtorLog").d(message)
                    }
                }
            }

            install(WebSockets)

            engine {
                preconfigured = get()
            }

        }
    }

    single {
        OkHttpClient
            .Builder().apply {
                cache(get())
                addInterceptor(get<ApiKeyInterceptor>())
                if (get(named("IsDebug"))) {
                    addInterceptor(get<HttpLoggingInterceptor>())
                    addNetworkInterceptor(FlipperOkhttpInterceptor(get()))
                }
            }
            .build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single { ApiKeyInterceptor(get(named("CryptoCompareApiKey"))) }

}
