package com.charliechristensen.coindetail.di

import androidx.lifecycle.SavedStateHandle
import com.charliechristensen.coindetail.CoinDetailInteractor
import com.charliechristensen.coindetail.CoinDetailViewModel
import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import dagger.Component
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


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

val coinDetailModule = module {
    viewModel { CoinDetailViewModel.ViewModel(get(), get(), "", SavedStateHandle()) }
    single { CoinDetailInteractor(get(), get()) }
}
