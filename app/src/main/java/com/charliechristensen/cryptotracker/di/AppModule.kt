package com.charliechristensen.cryptotracker.di

import android.content.Context
import android.content.SharedPreferences
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.common.LiveUpdatePriceClient
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.common.navigator.NavigatorImpl
import com.charliechristensen.cryptotracker.cryptotracker.BuildConfig
import com.charliechristensen.cryptotracker.cryptotracker.Database
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer.MainActivityViewModel
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.SqlDelightRepository
import com.charliechristensen.cryptotracker.data.models.db.DbCoinHistory
import com.charliechristensen.cryptotracker.data.models.ui.CoinHistoryTimePeriod
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.cryptotracker.data.preferences.AppPreferencesImpl
import com.charliechristensen.remote.di.remoteModule
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val appModule = module {

    single(named("BaseUrl")) { androidContext().getString(R.string.base_url) }

    single(named("WebSocketUrl")) { androidContext().getString(R.string.web_socket_url) }

    single(named("WebSocketUrlV2")) { androidContext().getString(R.string.web_socket_url_v2) }

    single(named("IsDebug")) { BuildConfig.DEBUG }

    single(named("CryptoCompareApiKey")) { BuildConfig.CRYPTOCOMPARE_API_KEY }

    single<Repository> { SqlDelightRepository(get(), get(), get()) }

    single<Navigator> { NavigatorImpl() }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            "cryptotracker-shared-preferences",
            Context.MODE_PRIVATE
        )
    }

    single<AppPreferences> { AppPreferencesImpl(get()) }

    single<SqlDriver> {
        AndroidSqliteDriver(Database.Schema, androidContext(), "cryptotracker-db.db")
    }

    single {
        Database(
            get(), DbCoinHistory.Adapter(
            timePeriodAdapter = CoinHistoryTimePeriod.databaseAdapter
        )
        )
    }

    single<FlipperClient> {
        AndroidFlipperClient.getInstance(androidContext()).apply {
            addPlugin(get<InspectorFlipperPlugin>())
            addPlugin(get<NetworkFlipperPlugin>())
        }
    }

    single {
        InspectorFlipperPlugin(
            androidContext(),
            DescriptorMapping.withDefaults()
        )
    }

    single { NetworkFlipperPlugin() }

    single { FormatterFactory() }

    single { LiveUpdatePriceClient(get()) }

    viewModel { MainActivityViewModel.ViewModel(get(), get(), get()) }

}.plus(remoteModule)
