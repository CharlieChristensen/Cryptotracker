package com.charliechristensen.cryptotracker.common.navigation

import androidx.annotation.NavigationRes

interface NavGraphHolder {

    fun addGraph(@NavigationRes navResource: Int)

    fun getGraphs(): List<Int>

}