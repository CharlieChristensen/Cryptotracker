package com.charliechristensen.cryptotracker

import androidx.appcompat.app.AppCompatDelegate
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.cryptotracker.BuildConfig
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.workers.FetchCoinListWorker
import com.charliechristensen.cryptotracker.di.appModule
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.soloader.SoLoader
import com.google.android.play.core.splitcompat.SplitCompatApplication
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainApplication : SplitCompatApplication(), ImageLoaderFactory {

    private val scope = MainScope()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        val repository: Repository = get()
        repository.theme()
            .map { it.styleId }
            .onEach(AppCompatDelegate::setDefaultNightMode)
            .launchIn(scope)

        setupWorkers()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            if (FlipperUtils.shouldEnableFlipper(this)) {
                SoLoader.init(this, false)
                val flipper: FlipperClient = get()
                flipper.start()
            }
        }
    }

    private fun setupWorkers() {
        val coinRefreshWorker = PeriodicWorkRequestBuilder<FetchCoinListWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "CoinRefreshWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                coinRefreshWorker
            )
    }

    override fun newImageLoader(): ImageLoader = get()

}
