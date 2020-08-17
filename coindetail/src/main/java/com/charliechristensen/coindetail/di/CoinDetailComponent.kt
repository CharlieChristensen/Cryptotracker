package com.charliechristensen.coindetail.di

import com.charliechristensen.coindetail.CoinDetailViewModel
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import dagger.Component


@PerModuleScope
@Component(
    dependencies = [AppComponent::class],
    modules = [CoinDetailModule::class]
)
interface CoinDetailComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): CoinDetailComponent
    }
    val coinDetailViewModelFactory: CoinDetailViewModel.ViewModel.Factory
}
