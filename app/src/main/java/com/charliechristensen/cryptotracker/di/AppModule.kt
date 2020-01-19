package com.charliechristensen.cryptotracker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.common.navigator.NavigatorImpl
import com.charliechristensen.cryptotracker.cryptotracker.BuildConfig
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.SqlDelightRepository
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.cryptotracker.data.preferences.AppPreferencesImpl
import com.charliechristensen.database.di.DatabaseModule
import com.charliechristensen.remote.di.RemoteModule
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
        repositoryImpl: SqlDelightRepository
    ): Repository = repositoryImpl

    @Provides
    @Singleton
    fun provideNavigator(
        navigator: NavigatorImpl
    ): Navigator = navigator

    @Provides
    @Singleton
    fun provideSharedPreferences(applicationContext: Context): SharedPreferences =
        applicationContext.getSharedPreferences(
            "cryptotracker-shared-preferences",
            Context.MODE_PRIVATE
        )

    @ExperimentalCoroutinesApi
    @Provides
    @Reusable
    fun provideAppPreferences(appPreferencesImpl: AppPreferencesImpl): AppPreferences =
        appPreferencesImpl
}
