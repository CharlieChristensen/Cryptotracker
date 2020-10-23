package com.charliechristensen.coindetail.di

import androidx.lifecycle.SavedStateHandle
import com.charliechristensen.coindetail.CoinDetailInteractor
import com.charliechristensen.coindetail.CoinDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun getCoinDetailModule(coinSymbol: String) = module {
    viewModel { CoinDetailViewModel.ViewModel(get(), get(), coinSymbol, SavedStateHandle()/*TODO*/) }
    single { CoinDetailInteractor(get(), get()) }
}
