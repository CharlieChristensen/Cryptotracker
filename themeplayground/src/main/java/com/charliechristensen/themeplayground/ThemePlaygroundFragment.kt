package com.charliechristensen.themeplayground

import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.themeplayground.di.DaggerThemePlaygroundComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

class ThemePlaygroundFragment : BaseFragment<ThemePlaygroundViewModel>(R.layout.fragment_theme_playground) {

    @ExperimentalCoroutinesApi
    override val viewModel: ThemePlaygroundViewModel by viewModel {
        DaggerThemePlaygroundComponent.builder()
            .appComponent(injector)
            .build()
            .themePlaygroundViewModel
    }
}
