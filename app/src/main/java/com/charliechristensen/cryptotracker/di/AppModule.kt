package com.charliechristensen.cryptotracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.charliechristensen.cryptotracker.common.navigation.NavGraphHolder
import com.charliechristensen.cryptotracker.common.navigation.NavGraphHolderImpl
import com.charliechristensen.cryptotracker.cryptotracker.BuildConfig
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.RepositoryImpl
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.cryptotracker.data.preferences.AppPreferencesImpl
import com.charliechristensen.database.DatabaseApi
import com.charliechristensen.database.di.DatabaseModule
import com.charliechristensen.remote.di.RemoteModule
import com.charliechristensen.remote.webservice.CryptoService
import com.charliechristensen.remote.websocket.WebSocketService
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Named
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Suppress("unused")
@AssistedModule
@Module(includes = [AssistedInject_AppModule::class, RemoteModule::class, DatabaseModule::class])
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
    @Named("IsDebug")
    fun provideIsDebug(): Boolean = BuildConfig.DEBUG

    @Provides
    @Singleton
    fun provideRepository(
        cryptoService: CryptoService,
        databaseApi: DatabaseApi,
        webSocket: WebSocketService
    ): Repository = RepositoryImpl(
        cryptoService,
        databaseApi,
        webSocket
    )

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
