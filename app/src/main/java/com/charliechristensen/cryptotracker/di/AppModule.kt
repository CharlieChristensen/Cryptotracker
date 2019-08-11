package com.charliechristensen.cryptotracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.charliechristensen.cryptotracker.common.AppPreferences
import com.charliechristensen.cryptotracker.common.AppPreferencesImpl
import com.charliechristensen.cryptotracker.cryptotracker.BuildConfig
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.database.AppDatabase
import com.charliechristensen.cryptotracker.data.webservice.CryptoService
import com.charliechristensen.cryptotracker.data.websocket.WebSocketService
import com.squareup.inject.assisted.dagger2.AssistedModule
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Suppress("unused")
@AssistedModule
@Module(includes = [AssistedInject_AppModule::class])
object AppModule {

    @Provides
    @JvmStatic
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    @JvmStatic
    @Named("BaseUrl")
    fun provideBaseUrl(context: Context): String = context.getString(R.string.base_url)

    @Provides
    @JvmStatic
    @Named("WebSocketUrl")
    fun provideWebSocketUrl(context: Context): String = context.getString(R.string.web_socket_url)

    @Provides
    @Singleton
    @JvmStatic
    fun provideRepository(
        cryptoService: CryptoService,
        database: AppDatabase
    ): Repository = Repository(
        cryptoService,
        database.coinDao(),
        database.coinPriceDao(),
        database.portfolioCoinDao(),
        database.combinedTableDao()
    )

    @Provides
    @Singleton
    @JvmStatic
    fun provideDatabase(applicationContext: Context): AppDatabase =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "coin-database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @JvmStatic
    fun provideApiService(retrofit: Retrofit): CryptoService =
        retrofit.create(CryptoService::class.java)

    @Provides
    @Singleton
    @JvmStatic
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    @JvmStatic
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
        return logging
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideRetrofit(
        @Named("BaseUrl") baseUrl: String,
        okHttpClient: OkHttpClient, moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    @JvmStatic
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    @JvmStatic
    fun provideWebSocket(@Named("WebSocketUrl") webSocketUrl: String): WebSocketService =
        WebSocketService(webSocketUrl)

    @Provides
    @Singleton
    @JvmStatic
    fun provideSharedPreferences(applicationContext: Context): SharedPreferences =
        applicationContext.getSharedPreferences(
            "cryptotracker-shared-preferences",
            Context.MODE_PRIVATE
        )

    @Provides
    @Reusable
    @JvmStatic
    fun provideAppPreferences(appPreferencesImpl: AppPreferencesImpl): AppPreferences =
        appPreferencesImpl

}
