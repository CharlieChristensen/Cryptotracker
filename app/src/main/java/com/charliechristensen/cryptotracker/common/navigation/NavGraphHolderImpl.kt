package com.charliechristensen.cryptotracker.common.navigation

import javax.inject.Inject

class NavGraphHolderImpl @Inject constructor() : NavGraphHolder {

    private val graphs = mutableListOf<Int>()

    override fun addGraph(navResource: Int) {
        graphs.add(navResource)
    }

    override fun getGraphs(): List<Int> = graphs

}