package com.charliechristensen.cryptotracker

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.charliechristensen.cryptotracker.data.workers.FetchCoinListWorker
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.DaggerAppComponent
import com.google.android.play.core.splitcompat.SplitCompatApplication
import java.util.concurrent.TimeUnit


class MainApplication : SplitCompatApplication() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(this)
    }

    override fun onCreate() {
        super.onCreate()
        setupWorkers()
    }

    private fun setupWorkers() {
        val coinRefreshWorker = PeriodicWorkRequestBuilder<FetchCoinListWorker>(24, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "CoinRefreshWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                coinRefreshWorker
            )
    }
}
