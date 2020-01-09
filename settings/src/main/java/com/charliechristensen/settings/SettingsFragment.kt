package com.charliechristensen.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.settings.databinding.DialogChooseThemeBinding
import com.charliechristensen.settings.databinding.ViewSettingsBinding
import com.charliechristensen.settings.di.DaggerSettingsComponent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import ru.ldralighieri.corbind.view.clicks
import ru.ldralighieri.corbind.widget.checkedChanges

@ExperimentalCoroutinesApi
class SettingsFragment : BaseFragment<SettingsViewModel.ViewModel>(R.layout.view_settings) {

    override val viewModel: SettingsViewModel.ViewModel by viewModel {
        DaggerSettingsComponent.factory()
            .create(injector)
            .settingsViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = ViewSettingsBinding.bind(view)

        binding.liveUpdatePricesButton.clicks()
            .bind { binding.liveUpdatePricesSwitch.toggle() }

        binding.setThemeButton.clicks()
            .bind { viewModel.inputs.themeButtonClicked() }

        binding.liveUpdatePricesSwitch.checkedChanges()
            .drop(1)
            .bind { viewModel.inputs.liveUpdatePricesToggled(it) }

        viewModel.outputs.showChooseThemeDialog
            .bind { showSelectThemeDialog(activity, it) }

        viewModel.outputs.liveUpdatePrices
            .bind {
                binding.liveUpdatePricesSwitch.isChecked = it
            }

        viewModel.outputs.themeDisplay
            .bind { binding.themeNameTextView.setText(it) }
    }

    @SuppressLint("InflateParams")
    private fun showSelectThemeDialog(
        activity: Activity?,
        selectedRadioButtonId: Int
    ) {
        if (activity == null) return
        val binding = DialogChooseThemeBinding.inflate(activity.layoutInflater)
        binding.radioGroup.check(selectedRadioButtonId)
//        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_choose_theme, null).apply {
//            radioGroup.check(selectedRadioButtonId)
//        }
        MaterialAlertDialogBuilder(activity)
            .setTitle("Choose Theme")
            .setView(binding.root)
            .setPositiveButton("OK") { dialog, _ ->
                viewModel.inputs.themeChosen(binding.radioGroup.checkedRadioButtonId)
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }
}
