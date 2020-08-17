package com.charliechristensen.settings

import android.os.Bundle
import android.view.View
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.settings.databinding.ViewSettingsBinding
import com.charliechristensen.settings.di.DaggerSettingsComponent
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SettingsFragment : BaseFragment<SettingsViewModel.ViewModel, ViewSettingsBinding>(R.layout.view_settings) {

    override val viewModel: SettingsViewModel.ViewModel by viewModel {
        DaggerSettingsComponent.factory()
            .create(injector)
            .settingsViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        viewModel.outputs.showChooseThemeDialog
            .bind(this::showSelectThemeDialog)

        viewModel.outputs.showCurrencyDialog
            .bind(this::showCurrencyDialog)

    }

    private fun showSelectThemeDialog(themes: List<AppTheme>) {
        val themeDisplayStrings = themes
            .map { resources.getString(it.displayId, "") }
            .toTypedArray()
        MaterialAlertDialogBuilder(activity)
            .setTitle("Theme")
            .setItems(themeDisplayStrings) { _, which ->
                viewModel.inputs.themeChosen(themes[which])
            }
            .show()
    }

    private fun showCurrencyDialog(availableCurrencies: Array<String>) {
        MaterialAlertDialogBuilder(activity)
            .setTitle("Currency")
            .setItems(availableCurrencies) { _, which ->
                viewModel.inputs.setCurrency(availableCurrencies[which])
            }
            .show()
    }
}
