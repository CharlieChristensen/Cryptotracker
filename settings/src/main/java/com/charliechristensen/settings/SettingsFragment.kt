package com.charliechristensen.settings

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.settings.di.DaggerSettingsComponent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_choose_theme.view.*
import kotlinx.android.synthetic.main.view_settings.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class SettingsFragment : BaseFragment<SettingsViewModel.ViewModel>(R.layout.view_settings) {

    override val viewModel: SettingsViewModel.ViewModel by viewModel {
        DaggerSettingsComponent.builder()
            .appComponent(injector)
            .build()
            .settingsViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveUpdatePricesButton.setOnClickListener {
            liveUpdatePricesSwitch.toggle()
        }

        setThemeButton.setOnClickListener {
            viewModel.inputs.themeButtonClicked()
        }

        liveUpdatePricesSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.inputs.liveUpdatePricesToggled(isChecked)
        }

        viewModel.outputs.showChooseThemeDialog
            .bind { showSelectThemeDialog(activity, it) }

        viewModel.outputs.liveUpdatePrices
            .bind { liveUpdatePricesSwitch.isChecked = it }

        viewModel.outputs.themeDisplay
            .bind { themeNameTextView.setText(it) }

    }

    private fun showSelectThemeDialog(
        activity: Activity?,
        selectedRadioButtonId: Int
    ) {
        if (activity == null) return
        val radioGroupView =
            activity.layoutInflater.inflate(R.layout.dialog_choose_theme, null)
        val radioGroup = radioGroupView.radioGroup
        radioGroup.check(selectedRadioButtonId)
        MaterialAlertDialogBuilder(activity)
            .setTitle("Choose Theme")
            .setView(radioGroupView)
            .setPositiveButton("OK") { _, _ ->
                viewModel.inputs.themeChosen(radioGroup.checkedRadioButtonId)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

}
