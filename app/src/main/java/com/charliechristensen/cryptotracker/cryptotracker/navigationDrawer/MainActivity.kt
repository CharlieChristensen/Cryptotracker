package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.skip
import com.charliechristensen.cryptotracker.common.extensions.viewModel
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
        setupNavigationMenus()

        viewModel.outputs.theme
            .skip(1)
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

    private fun setupNavigationMenus() {
        navigationView.setupWithNavController(navController)
        val navigationMenu = navigationView.menu.apply {
            add(R.id.navigationGroup, R.id.portfolioRoot, 0, R.string.my_portfolio).apply {
                setIcon(R.drawable.ic_chart_24dp)
            }
            add(R.id.navigationGroup, R.id.coinsRoot, 1, R.string.all_coins).apply {
                setIcon(R.drawable.ic_menu_coin)
            }
            add(R.id.navigationGroup, R.id.settingsRoot, 2, R.string.action_settings).apply {
                setIcon(R.drawable.ic_menu_settings)
            }
        }
        val appBarConfiguration = AppBarConfiguration.Builder(navigationMenu)
            .setDrawerLayout(drawerLayout)
            .build()
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
