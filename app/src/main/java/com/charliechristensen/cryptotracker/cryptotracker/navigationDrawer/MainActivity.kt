package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.injector
import com.charliechristensen.cryptotracker.common.ui.BaseActivity
import com.charliechristensen.cryptotracker.common.viewModel
import com.charliechristensen.cryptotracker.cryptotracker.R
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*

class MainActivity : BaseActivity() {

    private val viewModel: MainActivityViewModel.ViewModel by viewModel {
        injector.mainActivityViewModel
    }

    override val layoutResource: Int
        get() = R.layout.activity_navigation_drawer

    private val navController: NavController
        get() = findNavController(R.id.navigationHost)

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = viewModel.outputs.getAppThemeSync()
        changeTheme(theme, false)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        navigationView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration.Builder(R.id.portfolioRoot, R.id.coinsRoot, R.id.settingsRoot)
            .setDrawerLayout(drawerLayout)
            .build()
        toolbar.setupWithNavController(navController, appBarConfiguration)

        viewModel.outputs.theme()
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

}
