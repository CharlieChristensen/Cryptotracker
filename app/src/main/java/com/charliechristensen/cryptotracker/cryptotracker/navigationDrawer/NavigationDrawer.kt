package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.charliechristensen.cryptotracker.common.*
import com.charliechristensen.cryptotracker.common.ui.BaseActivity
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.cryptotracker.coinList.SearchCoinsListFragment
import com.charliechristensen.cryptotracker.cryptotracker.portfolio.PortfolioFragment
import com.charliechristensen.cryptotracker.cryptotracker.settings.SettingsFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*


class NavigationDrawer : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewModel: NavigationDrawerViewModel.ViewModel by viewModel {
        injector.navigationDrawerViewModel
    }

    private val actionBarDrawerToggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = viewModel.outputs.getAppThemeSync()
        changeTheme(theme, false)
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        if(savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.contentView, PortfolioFragment.newInstance())
                .commit()
            navigationView.setCheckedItem(R.id.nav_my_portfolio)
        }
        viewModel.outputs.navigateToPortfolio()
            .bind { setPortfolioAsRoot() }

        viewModel.outputs.navigateToSearchCoins()
            .bind { setSearchCoinsAsRoot() }

        viewModel.outputs.navigateToSettings()
            .bind { setSettingsAsRoot() }

        viewModel.outputs.theme()
            .bind { changeTheme(it, true) }

        supportFragmentManager.addOnBackStackChangedListener {
            setActionBarNavigationButton(actionBarDrawerToggle)
        }

        actionBarDrawerToggle.setToolbarNavigationClickListener {
            supportFragmentManager.popBackStack()
        }
        navigationView.setNavigationItemSelectedListener(this)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override val layoutResource: Int
        get() = R.layout.activity_navigation_drawer

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        viewModel.inputs.navigationItemSelected(item.itemId)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //region Navigation

    private fun setPortfolioAsRoot() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentView, PortfolioFragment.newInstance())
            .commit()
    }

    private fun setSearchCoinsAsRoot() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentView, SearchCoinsListFragment.newInstance(false))
            .commit()
    }

    private fun setSettingsAsRoot() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contentView, SettingsFragment.newInstance())
            .commit()
    }

    //endregion

    private fun setActionBarNavigationButton(drawerToggle: ActionBarDrawerToggle?) {
        if (supportFragmentManager.backStackEntryCount > 0) {
            drawerToggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            drawerToggle?.isDrawerIndicatorEnabled = true
        }
    }

}
