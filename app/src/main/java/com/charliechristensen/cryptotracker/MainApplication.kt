package com.charliechristensen.cryptotracker

import android.app.Application
import android.util.Log
import com.charliechristensen.cryptotracker.common.AppPreferences
import com.charliechristensen.cryptotracker.common.Constants
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.DaggerAppComponent
import com.charliechristensen.network.socketio.WebSocketService
import com.squareup.leakcanary.LeakCanary
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(this)
    }

    @Inject
    lateinit var repository: Repository
    @Inject
    lateinit var appPreferences: AppPreferences
    @Inject
    lateinit var webSocket: WebSocketService

    override fun onCreate() {
        appComponent.inject(this)
        super.onCreate()
        repository.refreshCoinListIfNeeded()
            .subscribeOn(Schedulers.io())
            .subscribe()
        monitorLiveUpdatePrices()

        RxJavaPlugins.setErrorHandler {
            //TODO
        }


        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun monitorLiveUpdatePrices(){
        appPreferences.liveUpdatePrices()
            .switchMap {
                if (it) {
                    repository.getPortfolioCoinSymbols()
                } else {
                    webSocket.disconnect()
                    Observable.empty<List<String>>()
                }
            }
            .subscribeOn(Schedulers.io())
            .subscribeBy(onNext = { symbolsList ->
                webSocket.connect { socket ->
                    socket.setPortfolioSubscriptions(symbolsList, Constants.MyCurrency)
                }
            }, onError = {
                Log.d("SOCKET IO ERROR", it.localizedMessage)
            })
    }

}