package com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.charliechristensen.cryptotracker.common.extensions.hideKeyboard
import com.charliechristensen.cryptotracker.common.extensions.navigateRight
import com.charliechristensen.cryptotracker.common.ui.BaseActivity
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.cryptotracker.databinding.ActivityNavigationDrawerBinding
import com.charliechristensen.cryptotracker.cryptotracker.databinding.AppBarNavigationDrawerBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.ldralighieri.corbind.navigation.destinationChanges

@ExperimentalCoroutinesApi
class MainActivity : BaseActivity() {

    private val viewModel: MainActivityViewModel.ViewModel by viewModel()

    private val navController: NavController
        get() {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navigationHost) as NavHostFragment
            return navHostFragment.navController
        }

    private val binding: AppBarNavigationDrawerBinding by lazy {
        AppBarNavigationDrawerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupNavigationMenus()

        navController.destinationChanges()
            .bind { hideKeyboard() }

        viewModel.outputs.navigationEvents
            .bind(navController::navigateRight)
    }

//    override fun onSupportNavigateUp(): Boolean = navigateUp(navController, binding.drawerLayout)

//    override fun onBackPressed() {
//        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            binding.drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }

    private fun setupNavigationMenus() {
        val navigationMenu = binding.bottomNavigation.menu.apply {
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
//        binding.navigationView.setupWithNavController(navController)
//        val navigationMenu = binding.navigationView.menu.apply {
//            add(R.id.navigationGroup, R.id.portfolioRoot, 0, R.string.my_portfolio).apply {
//                setIcon(R.drawable.ic_chart_24dp)
//            }
//            add(R.id.navigationGroup, R.id.coinsRoot, 1, R.string.all_coins).apply {
//                setIcon(R.drawable.ic_menu_coin)
//            }
//            add(R.id.navigationGroup, R.id.settingsRoot, 2, R.string.action_settings).apply {
//                setIcon(R.drawable.ic_menu_settings)
//            }
//        }
        val appBarConfiguration = AppBarConfiguration.Builder(navigationMenu)
//            .setOpenableLayout(binding.drawerLayout)
            .build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
