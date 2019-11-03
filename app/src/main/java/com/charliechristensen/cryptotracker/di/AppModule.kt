package com.charliechristensen.cryptotracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.charliechristensen.cryptotracker.common.navigation.NavGraphHolder
import com.charliechristensen.cryptotracker.common.navigation.NavGraphHolderImpl
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.cryptotracker.data.preferences.AppPreferencesImpl
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Suppress("unused")
@AssistedModule
@Module(includes = [AssistedInject_AppModule::class])
object AppModule {

    @Provides
    fun provideApplicationContext(application: Application): Context =
        application.applicationContext

    @Provides
    @Named("BaseUrl")
    fun provideBaseUrl(context: Context): String = context.getString(R.string.base_url)

    @Provides
    @Named("WebSocketUrl")
    fun provideWebSocketUrl(context: Context): String = context.getString(R.string.web_socket_url)

    @Provides
    @Singleton
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
    fun provideDatabase(applicationContext: Context): AppDatabase =
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "coin-database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): CryptoService =
        retrofit.create(CryptoService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
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
    fun provideRetrofit(
        @Named("BaseUrl") baseUrl: String,
        okHttpClient: OkHttpClient, moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideWebSocket(@Named("WebSocketUrl") webSocketUrl: String): WebSocketService =
        WebSocketService(webSocketUrl)

    @Provides
    @Singleton
    fun provideSharedPreferences(applicationContext: Context): SharedPreferences =
        applicationContext.getSharedPreferences(
            "cryptotracker-shared-preferences",
            Context.MODE_PRIVATE
        )

    @Provides
    @Reusable
    fun provideAppPreferences(appPreferencesImpl: AppPreferencesImpl): AppPreferences =
        appPreferencesImpl

    @Provides
    @Singleton
    fun provideNavGraphHolder(navGraphHolder: NavGraphHolderImpl): NavGraphHolder =
        navGraphHolder

}
