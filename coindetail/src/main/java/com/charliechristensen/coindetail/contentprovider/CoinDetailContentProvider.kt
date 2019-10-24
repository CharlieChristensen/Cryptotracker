package com.charliechristensen.coindetail.contentprovider

import com.charliechristensen.coindetail.R
import com.charliechristensen.cryptotracker.common.navigation.NavigationGraphContentProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class CoinDetailContentProvider : NavigationGraphContentProvider() {
    override fun getNavigationGraph(): Int = R.navigation.coin_detail_navigation_graph
}
