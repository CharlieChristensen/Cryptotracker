package com.charliechristensen.cryptotracker.di

import android.app.Application
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.common.navigation.NavGraphHolder
import com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer.MainActivityViewModel
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
import com.charliechristensen.cryptotracker.data.websocket.WebSocketService
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun repository(): Repository
    fun webSocketService(): WebSocketService
    fun navGraphHolder(): NavGraphHolder
    fun appPreferences(): AppPreferences
    fun formatterFactory(): FormatterFactory

    val mainActivityViewModel: MainActivityViewModel.ViewModel
}
