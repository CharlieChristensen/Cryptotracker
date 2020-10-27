package com.charliechristensen.portfolio.di

import com.charliechristensen.portfolio.PortfolioCoinListViewModel
import com.charliechristensen.portfolio.PortfolioInteractor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val portfolioModule = module {
    viewModel { PortfolioCoinListViewModel.ViewModel(get(), PortfolioInteractor(get(), get())) }
}
