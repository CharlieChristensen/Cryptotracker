package com.charliechristensen.portfolio.contentprovider

import com.charliechristensen.cryptotracker.common.navigation.NavigationGraphContentProvider
import com.charliechristensen.portfolio.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class PortfolioContentProvider: NavigationGraphContentProvider() {
    override fun getNavigationGraph(): Int = R.navigation.portfolio_navigation_graph
}
