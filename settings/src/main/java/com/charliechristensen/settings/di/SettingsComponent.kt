package com.charliechristensen.settings.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.settings.SettingsViewModel
import dagger.Component

@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface SettingsComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): SettingsComponent
    }
    val settingsViewModel: SettingsViewModel.ViewModel
}
