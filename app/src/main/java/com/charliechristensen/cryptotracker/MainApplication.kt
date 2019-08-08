package com.charliechristensen.cryptotracker

import android.app.Application
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.DaggerAppComponent
import io.reactivex.plugins.RxJavaPlugins

class MainApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(this)
    }

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler {
            //TODO
        }
    }

}