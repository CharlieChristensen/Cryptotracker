package com.charliechristensen.portfolio.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.portfolio.PortfolioCoinListViewModel
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi

@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface PortfolioComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): PortfolioComponent
    }
    @ExperimentalCoroutinesApi
    val portfolioCoinListViewModel: PortfolioCoinListViewModel.ViewModel
}
