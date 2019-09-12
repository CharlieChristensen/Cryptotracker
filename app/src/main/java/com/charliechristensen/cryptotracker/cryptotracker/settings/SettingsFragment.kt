package com.charliechristensen.cryptotracker.cryptotracker.settings

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.charliechristensen.cryptotracker.common.extensions.fragment
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.cryptotracker.R
import kotlinx.android.synthetic.main.dialog_choose_theme.view.*
import kotlinx.android.synthetic.main.view_settings.*

class SettingsFragment : BaseFragment<SettingsViewModel.ViewModel>(R.layout.view_settings) {

    override val viewModel: SettingsViewModel.ViewModel by viewModel {
        injector.settingsViewModel
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

        viewModel.outputs.liveUpdatePrices()
            .bind { liveUpdatePricesSwitch.isChecked = it }

        viewModel.outputs.showChooseThemeDialog()
            .bind { showSelectThemeDialog(activity, it) }

        viewModel.outputs.themeDisplay()
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
        AlertDialog.Builder(activity)
            .setTitle("Choose Theme")
            .setView(radioGroupView)
            .setPositiveButton("OK") { _, _ ->
                viewModel.inputs.themeChosen(radioGroup.checkedRadioButtonId)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    companion object {
        fun newInstance(): SettingsFragment =
            fragment {}
    }

}