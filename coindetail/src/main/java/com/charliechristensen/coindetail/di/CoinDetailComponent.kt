package com.charliechristensen.coindetail.di

import com.charliechristensen.coindetail.CoinDetailViewModel
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@PerModuleScope
@Component(
    dependencies = [AppComponent::class],
    modules = [CoinDetailModule::class]
)
interface CoinDetailComponent {

    val coinDetailViewModelFactory: CoinDetailViewModel.ViewModel.Factory
}
