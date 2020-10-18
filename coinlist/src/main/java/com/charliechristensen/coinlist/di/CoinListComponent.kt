package com.charliechristensen.coinlist.di

import androidx.lifecycle.SavedStateHandle
import com.charliechristensen.coinlist.SearchCoinsInteractor
import com.charliechristensen.coinlist.SearchCoinsViewModel
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import dagger.Component
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

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
    val searchCoinsViewModelFactory: SearchCoinsViewModel.ViewModel.Factory
}

val coinListModule = module {
    viewModel { SearchCoinsViewModel.ViewModel(get(), get(), SavedStateHandle()/*TODO*/, false/*TODO*/) }
    single { SearchCoinsInteractor(get()) }
}
