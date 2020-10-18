package com.charliechristensen.portfolio.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.portfolio.PortfolioCoinListViewModel
import com.charliechristensen.portfolio.PortfolioInteractor
import dagger.Component
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface PortfolioComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): PortfolioComponent
    }
    val portfolioCoinListViewModel: PortfolioCoinListViewModel.ViewModel
}

val portfolioModule = module {
    single { PortfolioInteractor(get(), get()) }
    viewModel { PortfolioCoinListViewModel.ViewModel(get(), get()) }
}
