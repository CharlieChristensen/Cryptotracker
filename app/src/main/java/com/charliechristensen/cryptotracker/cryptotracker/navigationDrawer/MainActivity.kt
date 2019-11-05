package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import android.os.Bundle
import android.view.Menu
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.charliechristensen.cryptotracker.MainApplication
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.navigation.NavigationHelper
import com.charliechristensen.cryptotracker.common.ui.BaseActivity
import com.charliechristensen.cryptotracker.cryptotracker.R
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : BaseActivity(R.layout.activity_navigation_drawer) {

    private val viewModel: MainActivityViewModel.ViewModel by viewModel {
        injector.mainActivityViewModel
    }

    private val navController: NavController
        get() = findNavController(R.id.navigationHost)

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = viewModel.outputs.getAppThemeSync()
        changeTheme(theme, false)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        val navGraph = navController.navInflater.inflate(R.navigation.navigation_graph)
        val navGraphs = (application as MainApplication).appComponent.navGraphHolder().getGraphs()
        navGraphs.forEach { graphId ->
            val graph = navController.navInflater.inflate(graphId)
            navGraph.addAll(graph)
        }
        navGraph.startDestination = navGraph.find { it.hasDeepLink(NavigationHelper.portfolioUri()) }!!.id
        navController.graph = navGraph

        navigationView.setupWithNavController(navController)
        val navigationMenu = navigationView.menu
        setupNavigationMenu(navigationMenu)
        val appBarConfiguration =
            AppBarConfiguration.Builder(navigationMenu)
                .setDrawerLayout(drawerLayout)
                .build()
        toolbar.setupWithNavController(navController, appBarConfiguration)

        viewModel.outputs.theme
            .bind { changeTheme(it, true) }
    }

    override fun onSupportNavigateUp(): Boolean = navigateUp(navController, drawerLayout)

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun changeTheme(theme: AppTheme, recreate: Boolean) {
        setTheme(theme.styleId)
        if (recreate) {
            recreate()
        }
    }

    private fun setupNavigationMenu(menu: Menu) {
        val navGraph = navController.graph
        navGraph.find { it.hasDeepLink(NavigationHelper.portfolioUri()) }
            ?.let { navDestination ->
                val menuItem = menu.add(R.id.navigationGroup, navDestination.id, 0, R.string.my_portfolio)
                menuItem.setIcon(R.drawable.ic_chart_24dp)
            }

        navGraph.find { it.hasDeepLink(NavigationHelper.rootCoinListUri()) }
            ?.let { navDestination ->
                val menuItem = menu.add(R.id.navigationGroup, navDestination.id, 0, R.string.all_coins)
                menuItem.setIcon(R.drawable.ic_menu_coin)
            }

        navGraph.find { it.hasDeepLink(NavigationHelper.settingsUri()) }
            ?.let { navDestination ->
                val menuItem = menu.add(R.id.navigationGroup, navDestination.id, 0, R.string.action_settings)
                menuItem.setIcon(R.drawable.ic_menu_settings)
            }
    }

}
