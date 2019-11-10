package com.charliechristensen.cryptotracker.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.charliechristensen.cryptotracker.MainApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class FetchCoinListWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = (applicationContext as MainApplication).appComponent.repository()
        return try {
            repository.refreshCoinListIfNeeded()
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}
