package com.charliechristensen.settings.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.settings.SettingsViewModel
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface SettingsComponent {

    val settingsViewModel: SettingsViewModel.ViewModel

}
