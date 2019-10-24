package com.charliechristensen.coindetail.di

import com.charliechristensen.coindetail.CoinDetailViewModel
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@PerModuleScope
@Component(
    dependencies = [AppComponent::class],
    modules = [CoinDetailModule::class]
)
interface CoinDetailComponent {

    val coinDetailViewModelFactory: CoinDetailViewModel.ViewModel.Factory

}
