package com.charliechristensen.cryptotracker.cryptotracker.coinDetail

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.charliechristensen.cryptotracker.common.*
import com.charliechristensen.cryptotracker.common.extensions.getColorFromResource
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.jakewharton.rxbinding2.support.design.widget.selections
import kotlinx.android.synthetic.main.dialog_text_input_layout.view.*
import kotlinx.android.synthetic.main.view_coin_detail.*
import kotlinx.android.synthetic.main.view_line_graph.*

class CoinDetailFragment : BaseFragment<CoinDetailViewModel.ViewModel>() {

    override val viewModel: CoinDetailViewModel.ViewModel by viewModel {
        val coinSymbol = CoinDetailFragmentArgs.fromBundle(requireArguments()).coinSymbol
        injector.coinDetailViewModelFactory.create(coinSymbol)
    }

    override val layoutResource: Int
        get() = R.layout.view_coin_detail

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_TOGGLE_GROUP_SELECTED, dateTabLayout.selectedTabPosition)
        super.onSaveInstanceState(outState)
    }

    private fun restoreViewState(savedViewState: Bundle?) {
        val checkedId = savedViewState?.getInt(KEY_TOGGLE_GROUP_SELECTED) ?: 0
        dateTabLayout.getTabAt(checkedId)?.select()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreViewState(savedInstanceState)

        viewModel.outputs.isCoinInPortfolio()
            .bind { setButtonLayout(it) }

        viewModel.outputs.toolbarImageData()
            .bind { setToolbarImage(it.coinName, it.imageUrl) }

        viewModel.outputs.currentCoinPrice()
            .bind { currentPriceTextView.text = it }

        viewModel.outputs.low24Hour()
            .bind { low24HourTextView.text = it }

        viewModel.outputs.high24Hour()
            .bind { high24HourTextView.text = it }

        viewModel.outputs.walletUnitsOwned()
            .bind { walletAmountOwnedTextView.text = it }

        viewModel.outputs.walletTotalValue()
            .bind { walletTotalValueTextView.text = it }

        viewModel.outputs.walletPriceChange24Hour()
            .bind {
                walletPriceChange24Hour.text = it.value
                activity?.getColorFromResource(ColorUtils.getColorInt(it.color))?.let { color ->
                    walletPriceChange24Hour.setTextColor(color)
                }
            }

        viewModel.outputs.valueChange24Hour()
            .bind {
                percentChangeTextView.text = it.value
                activity?.getColorFromResource(ColorUtils.getColorInt(it.color))?.let { colorInt ->
                    percentChangeTextView.setTextColor(colorInt)
                }
            }

        viewModel.outputs.percentChangeTimePeriod()
            .bind { title24HourPercentChange.setText(it) }

        viewModel.outputs.graphState()
            .bind { setGraphState(it) }

        viewModel.outputs.showAddCoinDialog()
            .bind { showAddCoinDialog(activity, it) }

        viewModel.outputs.showEditCoinAmountDialog()
            .bind { showEditCoinAmountDialog(activity, it) }

        viewModel.outputs.showConfirmRemoveDialog()
            .bind { showConfirmRemoveDialog(activity, it) }

        viewModel.outputs.showNetworkError()
            .bind { showToast(R.string.error_network_error) }

        dateTabLayout.selections()
            .distinctUntilChanged()
            .map { it.position }
            .bind { viewModel.inputs.graphDateSelectionChanged(it) }

        addToPortfolioButton.setOnClickListener {
            viewModel.inputs.addCoinButtonClicked()
        }

        editQuantityButton.setOnClickListener {
            viewModel.inputs.editQuantityButtonClicked()
        }

        removeFromPortfolioButton.setOnClickListener {
            viewModel.inputs.removeFromPortfolioButtonClicked()
        }
    }

    //region View Helpers

    private fun setGraphState(graphState: CoinDetailGraphState) {
        val activity = activity ?: return
        when (graphState) {
            is CoinDetailGraphState.Success -> {
                val color = activity.getColorFromResource(ColorUtils.getColorInt(graphState.color))
                val title = activity.getString(R.string.history)
                lineGraphView?.setDataSet(graphState.coinHistoryList, title, color)
            }
            CoinDetailGraphState.Loading -> {
                lineGraphView?.showLoading()
                lineGraphView?.clear()
            }
            CoinDetailGraphState.NoData -> {
                lineGraphView?.showNoData()
            }
            CoinDetailGraphState.Error -> {
                lineGraphView?.showError()
            }
        }
    }

    private fun setToolbarImage(coinName: String, imageUrl: String?) {
        val activity = activity ?: return
        val appCompatActivity = activity as AppCompatActivity?
        val actionBar = appCompatActivity?.supportActionBar ?: return
        val styledAttributes = activity.getTheme().obtainStyledAttributes(
            intArrayOf(android.R.attr.actionBarSize)
        )
        val actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()
        val imageSize = actionBarHeight / 3
        if (imageSize <= 0) {
            return
        }
        GlideApp.with(activity)
            .asDrawable()
            .load(imageUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val imageSpan = ImageSpan(resource, ImageSpan.ALIGN_BASELINE)
                    imageSpan.drawable.setBounds(0, 0, imageSize, imageSize)
                    val stringBuilder = SpannableStringBuilder().apply {
                        append("  ")
                        setSpan(imageSpan, length - 1, length, 0)
                        append("  ")
                        append(coinName)
                    }
                    actionBar.title = stringBuilder
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    private fun setButtonLayout(inPortfolio: Boolean) {
        val currentButtonConstraintSet = ConstraintSet()
        currentButtonConstraintSet.clone(buttonConstraintLayout)
        val newButtonConstraintSet = ConstraintSet()
        if (inPortfolio) {
            newButtonConstraintSet.clone(
                activity,
                R.layout.view_coin_detail_button_layout_portfolio
            )
        } else {
            newButtonConstraintSet.clone(activity, R.layout.view_coin_detail_button_layout_add)
        }
        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator()
        newButtonConstraintSet.applyTo(buttonConstraintLayout)
        TransitionManager.beginDelayedTransition(constraintLayout, transition)
    }

    //endregion

    //region Dialogs

    private fun showAddCoinDialog(
        activity: Activity?,
        coinSymbol: String
    ) {
        if (activity == null) return
        val editTextView = activity.layoutInflater.inflate(R.layout.dialog_text_input_layout, null)
        val textInputLayout = editTextView.textInputLayout
        textInputLayout.hint = "Coin Amount"
        AlertDialog.Builder(activity)
            .setTitle(coinSymbol)
            .setMessage("How many coins do you own? (optional)")
            .setView(editTextView)
            .setPositiveButton("ADD") { _, _ ->
                val inputText = textInputLayout.editText?.text ?: ""
                val amount = inputText.toString().toDoubleOrNull() ?: 0.0
                viewModel.inputs.confirmAddCoinToPortfolioClicked(coinSymbol, amount)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun showEditCoinAmountDialog(
        activity: Activity?,
        coinSymbol: String
    ) {
        if (activity == null) return
        val editTextView = activity.layoutInflater.inflate(R.layout.dialog_text_input_layout, null)
        val textInputLayout = editTextView.textInputLayout
        textInputLayout.hint = "Coin Amount"
        AlertDialog.Builder(activity)
            .setTitle("Edit Amount")
            .setMessage("Set total amount of $coinSymbol owned")
            .setView(editTextView)
            .setPositiveButton("OK") { _, _ ->
                val inputText = textInputLayout.editText?.text ?: ""
                val amount = inputText.toString().toDoubleOrNull() ?: 0.0
                viewModel.inputs.confirmEditCoinAmountClicked(coinSymbol, amount)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun showConfirmRemoveDialog(
        activity: Activity?,
        coinSymbol: String
    ) {
        if (activity == null) return
        AlertDialog.Builder(activity)
            .setTitle("Remove")
            .setMessage("Are you sure you want to remove $coinSymbol from your portfolio?")
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.inputs.confirmRemoveCoinFromPortfolioClicked(coinSymbol)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    //endregion

    companion object {
        private const val KEY_TOGGLE_GROUP_SELECTED = "toggle_group_selected"
    }
}