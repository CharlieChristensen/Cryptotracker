package com.charliechristensen.coindetail

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import coil.imageLoader
import coil.request.ImageRequest
import com.charliechristensen.coindetail.databinding.DialogTextInputLayoutBinding
import com.charliechristensen.coindetail.databinding.ViewCoinDetailBinding
import com.charliechristensen.coindetail.di.getCoinDetailModule
import com.charliechristensen.cryptotracker.common.extensions.setColorValueString
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.cryptotracker.common.ui.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.module.Module


class CoinDetailFragment : BaseFragment(R.layout.view_coin_detail) {

    override val koinModule: Module by lazy {
        getCoinDetailModule(
            CoinDetailFragmentArgs.fromBundle(requireArguments()).coinSymbol
        )
    }

    private val viewModel: CoinDetailViewModel.ViewModel by viewModel()

    private val binding: ViewCoinDetailBinding by viewBinding(ViewCoinDetailBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.outputs.isCoinInPortfolio
            .bind { inPortfolio ->
                if (inPortfolio) {
                    binding.constraintLayout.transitionToState(R.id.editCoin)
                } else {
                    binding.constraintLayout.transitionToState(R.id.addCoin)
                }
            }

        viewModel.outputs.currentCoinPrice
            .bind(binding.currentPriceTextView::setText)

        viewModel.outputs.percentChangeTimePeriod
            .bind(binding.title24HourPercentChange::setText)

        viewModel.outputs.valueChange24Hour
            .bind(binding.percentChangeTextView::setColorValueString)

        viewModel.outputs.high24Hour
            .bind(binding.high24HourTextView::setText)

        viewModel.outputs.low24Hour
            .bind(binding.low24HourTextView::setText)

        viewModel.outputs.toolbarImageData
            .bind { setToolbarImage(it.coinName, it.imageUrl) }

        viewModel.outputs.selectedDateTab
            .bind(binding.lineGraphController.dateTabLayout::selectedTab)

        viewModel.outputs.walletTotalValue
            .bind(binding.walletTotalValueTextView::setText)

        viewModel.outputs.walletUnitsOwned
            .bind(binding.walletAmountOwnedTextView::setText)

        viewModel.outputs.walletPriceChange24Hour
            .bind(binding.walletPriceChange24Hour::setColorValueString)

        viewModel.outputs.showAddCoinDialog
            .bind(::showAddCoinDialog)

        viewModel.outputs.showEditCoinAmountDialog
            .bind(::showEditCoinAmountDialog)

        viewModel.outputs.showConfirmRemoveDialog
            .bind(::showConfirmRemoveDialog)

        viewModel.outputs.graphState
            .bind(binding.lineGraphController.lineGraphView::setGraphState)

        viewModel.outputs.showNetworkError
            .bind { showToast(com.charliechristensen.cryptotracker.cryptotracker.R.string.error_network_error) }

        binding.addToPortfolioButton
            .setOnClickListener { viewModel.inputs.addCoinButtonClicked() }

        binding.editQuantityButton
            .setOnClickListener { viewModel.inputs.editQuantityButtonClicked() }

        binding.removeFromPortfolioButton
            .setOnClickListener { viewModel.inputs.removeFromPortfolioButtonClicked() }

        binding.lineGraphController.dateTabLayout
            .addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewModel.inputs.graphDateSelectionChanged(tab.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
    }

    //region View Helpers

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
        val request = ImageRequest.Builder(activity)
            .lifecycle(this)
            .data(imageUrl)
            .target { drawable ->
                val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE)
                imageSpan.drawable.setBounds(0, 0, imageSize, imageSize)
                val stringBuilder = SpannableStringBuilder().apply {
                    append("  ")
                    setSpan(imageSpan, length - 1, length, 0)
                    append("  ")
                    append(coinName)
                }
                actionBar.title = stringBuilder
            }
            .build()
        activity.imageLoader.enqueue(request)
    }

    //endregion

    //region Dialogs

    private fun showAddCoinDialog(coinSymbol: String) {
        val activity = activity ?: return
        val binding = DialogTextInputLayoutBinding.inflate(activity.layoutInflater)
        val textInputLayout = binding.textInputLayout
        textInputLayout.hint = "Coin Amount"
        MaterialAlertDialogBuilder(activity)
            .setTitle(coinSymbol)
            .setMessage("How many coins do you own? (optional)")
            .setView(binding.root)
            .setPositiveButton("ADD") { _, _ ->
                val inputText = textInputLayout.editText?.text ?: ""
                val amount = inputText.toString().toDoubleOrNull() ?: 0.0
                viewModel.inputs.confirmAddCoinToPortfolioClicked(coinSymbol, amount)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun showEditCoinAmountDialog(coinSymbol: String) {
        val activity = activity ?: return
        val binding = DialogTextInputLayoutBinding.inflate(activity.layoutInflater)
        val textInputLayout = binding.textInputLayout
        textInputLayout.hint = "Coin Amount"
        MaterialAlertDialogBuilder(activity)
            .setTitle("Edit Amount")
            .setMessage("Set total amount of $coinSymbol owned")
            .setView(binding.root)
            .setPositiveButton("OK") { _, _ ->
                val inputText = textInputLayout.editText?.text ?: ""
                val amount = inputText.toString().toDoubleOrNull() ?: 0.0
                viewModel.inputs.confirmEditCoinAmountClicked(coinSymbol, amount)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun showConfirmRemoveDialog(coinSymbol: String) {
        val activity = activity ?: return
        MaterialAlertDialogBuilder(activity)
            .setTitle("Remove")
            .setMessage("Are you sure you want to remove $coinSymbol from your portfolio?")
            .setPositiveButton("DELETE") { _, _ ->
                viewModel.inputs.confirmRemoveCoinFromPortfolioClicked(coinSymbol)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    //endregion

}
