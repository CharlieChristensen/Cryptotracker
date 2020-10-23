package com.charliechristensen.coinlist.di

import androidx.lifecycle.SavedStateHandle
import com.charliechristensen.coinlist.SearchCoinsInteractor
import com.charliechristensen.coinlist.SearchCoinsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun getCoinListModule(filterOutOwnedCoins: Boolean) = module {
    viewModel {
        SearchCoinsViewModel.ViewModel(
            get(),
            get(),
            SavedStateHandle()/*TODO*/,
            filterOutOwnedCoins
        )
    }
    single { SearchCoinsInteractor(get()) }
}
