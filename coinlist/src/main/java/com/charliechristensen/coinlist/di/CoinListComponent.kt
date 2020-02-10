package com.charliechristensen.coinlist.di

import com.charliechristensen.coinlist.SearchCoinsViewModel
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@PerModuleScope
@Component(
    dependencies = [AppComponent::class],
    modules = [CoinListModule::class]
)
interface CoinListComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): CoinListComponent
    }
    @FlowPreview
    @ExperimentalCoroutinesApi
    val searchCoinsViewModelFactory: SearchCoinsViewModel.ViewModel.Factory

}
