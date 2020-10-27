package com.charliechristensen.settings

import android.os.Bundle
import android.view.View
import com.charliechristensen.cryptotracker.common.AppTheme
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.cryptotracker.common.ui.viewBinding
import com.charliechristensen.settings.databinding.ViewSettingsBinding
import com.charliechristensen.settings.di.settingsModule
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.module.Module


class SettingsFragment : BaseFragment(R.layout.view_settings) {

    override val koinModule: Module = settingsModule

    private val viewModel: SettingsViewModel.ViewModel by viewModel()

    private val binding: ViewSettingsBinding by viewBinding(ViewSettingsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.outputs.liveUpdatePrices
            .bind(binding.liveUpdatePricesSwitch::setChecked)

        viewModel.outputs.showChooseThemeDialog
            .bind(::showSelectThemeDialog)

        viewModel.outputs.showCurrencyDialog
            .bind(::showCurrencyDialog)

        viewModel.outputs.themeDisplay
            .bind(binding.themeNameTextView::setText)

        viewModel.outputs.displayCurrency
            .bind(binding.setCurrencyButtonDetail::setText)

        binding.liveUpdatePricesButton
            .setOnClickListener { binding.liveUpdatePricesSwitch.toggle() }

        binding.liveUpdatePricesSwitch
            .setOnCheckedChangeListener { _, isChecked ->
                viewModel.inputs.liveUpdatePricesToggled(isChecked)
            }

        binding.setThemeButton
            .setOnClickListener { viewModel.inputs.themeButtonClicked() }

        binding.setCurrencyButton
            .setOnClickListener { viewModel.inputs.currencyButtonClicked() }

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
