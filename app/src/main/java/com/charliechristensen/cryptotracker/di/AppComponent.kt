package com.charliechristensen.cryptotracker.di

import android.app.Application
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.common.navigator.Navigator
import com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer.MainActivityViewModel
import com.charliechristensen.cryptotracker.data.Repository
import com.facebook.flipper.core.FlipperClient
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun repository(): Repository
    fun navigator(): Navigator
    fun formatterFactory(): FormatterFactory
    fun flipper(): FlipperClient

    @ExperimentalCoroutinesApi
    val mainActivityViewModel: MainActivityViewModel.ViewModel
}
