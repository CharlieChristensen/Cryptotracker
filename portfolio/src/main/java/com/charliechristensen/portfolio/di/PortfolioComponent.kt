package com.charliechristensen.portfolio.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.portfolio.PortfolioCoinListViewModel
import dagger.Component

@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface PortfolioComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): PortfolioComponent
    }
    val portfolioCoinListViewModel: PortfolioCoinListViewModel.ViewModel
}
