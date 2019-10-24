package com.charliechristensen.settings.contentprovider

import com.charliechristensen.cryptotracker.common.navigation.NavigationGraphContentProvider
import com.charliechristensen.settings.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class SettingsContentProvider: NavigationGraphContentProvider() {

    override fun getNavigationGraph(): Int = R.navigation.settings_navigation_graph

}
