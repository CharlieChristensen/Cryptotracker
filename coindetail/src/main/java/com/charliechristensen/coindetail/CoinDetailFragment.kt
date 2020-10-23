package com.charliechristensen.coindetail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.charliechristensen.coindetail.databinding.DialogTextInputLayoutBinding
import com.charliechristensen.coindetail.databinding.ViewCoinDetailBinding
import com.charliechristensen.coindetail.di.getCoinDetailModule
import com.charliechristensen.cryptotracker.common.GlideApp
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.module.Module


class CoinDetailFragment :
    BaseFragment<CoinDetailViewModel.ViewModel, ViewCoinDetailBinding>(R.layout.view_coin_detail) {

    override val koinModule: Module by lazy {
        getCoinDetailModule(
            CoinDetailFragmentArgs.fromBundle(requireArguments()).coinSymbol
        )
    }

    override val viewModel: CoinDetailViewModel.ViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        viewModel.outputs.isCoinInPortfolio
            .bind { inPortfolio ->
                if (inPortfolio) {
                    binding.constraintLayout.transitionToState(R.id.editCoin)
                } else {
                    binding.constraintLayout.transitionToState(R.id.addCoin)
                }
            }

        viewModel.outputs.toolbarImageData
            .bind { setToolbarImage(it.coinName, it.imageUrl) }

        viewModel.outputs.showAddCoinDialog
            .bind(this::showAddCoinDialog)

        viewModel.outputs.showEditCoinAmountDialog
            .bind(this::showEditCoinAmountDialog)

        viewModel.outputs.showConfirmRemoveDialog
            .bind(this::showConfirmRemoveDialog)

        viewModel.outputs.showNetworkError
            .bind { showToast(com.charliechristensen.cryptotracker.cryptotracker.R.string.error_network_error) }
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
        GlideApp.with(this)
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
