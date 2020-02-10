package com.charliechristensen.themeplayground.di

import com.charliechristensen.cryptotracker.di.AppComponent
import com.charliechristensen.cryptotracker.di.PerModuleScope
import com.charliechristensen.themeplayground.ThemePlaygroundViewModel
import dagger.Component

@PerModuleScope
@Component(dependencies = [AppComponent::class])
interface ThemePlaygroundComponent {
    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent): ThemePlaygroundComponent
    }
    val themePlaygroundViewModel: ThemePlaygroundViewModel
}
