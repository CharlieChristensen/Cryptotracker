package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.extensions.hideKeyboard
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.navigateRight
import com.charliechristensen.cryptotracker.common.extensions.skip
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.ui.BaseActivity
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.cryptotracker.databinding.ActivityNavigationDrawerBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.navigation.destinationChanges

@ExperimentalCoroutinesApi
@FlowPreview
class MainActivity : BaseActivity() {

    private val viewModel: MainActivityViewModel.ViewModel by viewModel {
        injector.mainActivityViewModel
    }

    private val navController: NavController
        get() = findNavController(R.id.navigationHost)

    private val binding: ActivityNavigationDrawerBinding by lazy {
        ActivityNavigationDrawerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val theme = viewModel.outputs.getAppThemeSync()
        changeTheme(theme, false)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.mainView.toolbar)
        setupNavigationMenus()

        navController.destinationChanges()
            .onEach { hideKeyboard() }
            .launchIn(lifecycleScope)

        viewModel.outputs.theme
            .skip(1)
            .bind { changeTheme(it, true) }

        viewModel.outputs.navigationEvents
            .bind(navController::navigateRight)
    }

    override fun onSupportNavigateUp(): Boolean = navigateUp(navController, binding.drawerLayout)

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
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
        binding.navigationView.setupWithNavController(navController)
        val navigationMenu = binding.navigationView.menu.apply {
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
            .setDrawerLayout(binding.drawerLayout)
            .build()
        binding.mainView.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
