package com.charliechristensen.settings.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.settings.SettingsViewModel
import dagger.Component
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface SettingsComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): SettingsComponent
    }
    val settingsViewModel: SettingsViewModel.ViewModel
}

val settingsModule = module {
    viewModel { SettingsViewModel.ViewModel(get()) }
}
