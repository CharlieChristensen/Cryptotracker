package com.charliechristensen.portfolio.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.portfolio.PortfolioCoinListViewModel
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface PortfolioComponent {

    val portfolioCoinListViewModel: PortfolioCoinListViewModel.ViewModel

}
