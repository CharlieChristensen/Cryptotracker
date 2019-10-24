package com.charliechristensen.coinlist.contentprovider

import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.navigation.NavigationGraphContentProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class CoinListContentProvider: NavigationGraphContentProvider() {
    override fun getNavigationGraph(): Int = R.navigation.coin_list_navigation_graph
}
