package com.charliechristensen.cryptotracker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.charliechristensen.cryptotracker.cryptotracker.BuildConfig
import com.charliechristensen.cryptotracker.data.workers.FetchCoinListWorker
import com.charliechristensen.cryptotracker.di.appModule
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.soloader.SoLoader
import com.google.android.play.core.splitcompat.SplitCompatApplication
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainApplication : SplitCompatApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        setupWorkers()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            if (FlipperUtils.shouldEnableFlipper(this)) {
                SoLoader.init(this, false)
//                appComponent.flipper().start()
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
}
