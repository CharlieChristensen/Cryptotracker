package com.charliechristensen.cryptotracker

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.DaggerAppComponent
import com.google.android.play.core.splitcompat.SplitCompatApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class MainApplication : SplitCompatApplication() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(this)
    }

}
