package com.charliechristensen.cryptotracker.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.charliechristensen.cryptotracker.MainApplication
import com.charliechristensen.cryptotracker.data.Repository
import org.koin.android.ext.android.get

class FetchCoinListWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository: Repository = (applicationContext as MainApplication).get()
        return try {
            repository.refreshCoinListIfNeeded()
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}
