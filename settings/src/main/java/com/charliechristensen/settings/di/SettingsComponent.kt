package com.charliechristensen.settings.di

import com.charliechristensen.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel.ViewModel(get()) }
}
