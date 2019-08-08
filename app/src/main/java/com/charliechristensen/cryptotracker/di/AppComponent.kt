package com.charliechristensen.cryptotracker.di

import android.app.Application
import com.charliechristensen.cryptotracker.cryptotracker.coinDetail.CoinDetailViewModel
import com.charliechristensen.cryptotracker.cryptotracker.coinList.SearchCoinsViewModel
import com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer.MainActivityViewModel
import com.charliechristensen.cryptotracker.cryptotracker.portfolio.PortfolioCoinListViewModel
import com.charliechristensen.cryptotracker.cryptotracker.settings.SettingsViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    val mainActivityViewModel: MainActivityViewModel.ViewModel
    val coinDetailViewModelFactory: CoinDetailViewModel.ViewModel.Factory
    val portfolioCoinListViewModel: PortfolioCoinListViewModel.ViewModel
    val searchCoinsViewModelFactory: SearchCoinsViewModel.ViewModel.Factory
    val settingsViewModel: SettingsViewModel.ViewModel
}