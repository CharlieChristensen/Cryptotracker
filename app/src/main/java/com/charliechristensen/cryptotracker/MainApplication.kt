package com.charliechristensen.cryptotracker

import android.app.Application
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.DaggerAppComponent

class MainApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(this)
    }

}