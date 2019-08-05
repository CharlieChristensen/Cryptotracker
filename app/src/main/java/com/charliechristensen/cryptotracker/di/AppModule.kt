package com.charliechristensen.cryptotracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.MainThread
import androidx.room.Room
import com.charliechristensen.cryptotracker.common.AppPreferences
import com.charliechristensen.cryptotracker.common.AppPreferencesImpl
import com.charliechristensen.cryptotracker.cryptotracker.BuildConfig
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.database.AppDatabase
import com.charliechristensen.network.CryptoService
import com.charliechristensen.network.CryptoServiceFactory
import com.charliechristensen.network.socketio.WebSocketService
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Named
import javax.inject.Singleton

@Suppress("unused")
@AssistedModule
@Module(includes = [AssistedInject_AppModule::class])
object AppModule {

    @Provides
    @Singleton
    @JvmStatic
    fun provideApplicationContext(application: Application): Context = application.applicationContext

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
    ): Repository =
        Repository(
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
    @Singleton
    @JvmStatic
    fun provideApiService(@Named("BaseUrl") baseUrl: String): CryptoService =
        CryptoServiceFactory.makeCryptoService(baseUrl, BuildConfig.DEBUG)

    @Provides
    @Singleton
    @JvmStatic
    fun provideWebSocket(@Named("WebSocketUrl") webSocketUrl: String): WebSocketService =
        WebSocketService(webSocketUrl)

    @Provides
    @Singleton
    @JvmStatic
    fun provideSharedPreferences(applicationContext: Context): SharedPreferences =
        applicationContext.getSharedPreferences("cryptotracker-shared-preferences", Context.MODE_PRIVATE)

    @Provides
    @Reusable
    @JvmStatic
    fun provideAppPreferences(appPreferencesImpl: AppPreferencesImpl): AppPreferences =
        appPreferencesImpl

}
