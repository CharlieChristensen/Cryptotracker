package com.charliechristensen.cryptotracker.di

import android.app.Application
import com.charliechristensen.cryptotracker.common.FormatterFactory
import com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer.MainActivityViewModel
import com.charliechristensen.cryptotracker.data.Repository
import com.charliechristensen.cryptotracker.data.preferences.AppPreferences
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
    fun appPreferences(): AppPreferences
    fun formatterFactory(): FormatterFactory

    @ExperimentalCoroutinesApi
    val mainActivityViewModel: MainActivityViewModel.ViewModel
}
