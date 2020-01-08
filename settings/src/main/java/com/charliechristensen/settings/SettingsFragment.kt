package com.charliechristensen.settings

import android.annotation.SuppressLint
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

        liveUpdatePricesButton.clicks()
            .bind { liveUpdatePricesSwitch.toggle() }

        setThemeButton.clicks()
            .bind { viewModel.inputs.themeButtonClicked() }

        liveUpdatePricesSwitch.checkedChanges()
            .bind { viewModel.inputs.liveUpdatePricesToggled(it) }

        viewModel.outputs.showChooseThemeDialog
            .bind { showSelectThemeDialog(activity, it) }

        viewModel.outputs.liveUpdatePrices
            .bind { liveUpdatePricesSwitch.isChecked = it }

        viewModel.outputs.themeDisplay
            .bind { themeNameTextView.setText(it) }
    }

    @SuppressLint("InflateParams")
    private fun showSelectThemeDialog(
        activity: Activity?,
        selectedRadioButtonId: Int
    ) {
        if (activity == null) return
        val dialogView = activity.layoutInflater.inflate(R.layout.dialog_choose_theme, null).apply {
            radioGroup.check(selectedRadioButtonId)
        }
        MaterialAlertDialogBuilder(activity)
            .setTitle("Choose Theme")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                viewModel.inputs.themeChosen(dialogView.radioGroup.checkedRadioButtonId)
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }
}
