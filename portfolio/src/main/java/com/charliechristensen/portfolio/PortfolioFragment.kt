package com.charliechristensen.portfolio

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.charliechristensen.cryptotracker.common.ColorUtils
import com.charliechristensen.cryptotracker.common.extensions.getColorFromResource
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.navigation.NavigationHelper
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.portfolio.di.DaggerPortfolioComponent
import com.charliechristensen.portfolio.list.PortfolioAdapter
import kotlinx.android.synthetic.main.view_portfolio_coin_list.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class PortfolioFragment :
    BaseFragment<PortfolioCoinListViewModel.ViewModel>(R.layout.view_portfolio_coin_list) {

    override val viewModel: PortfolioCoinListViewModel.ViewModel by viewModel {
        DaggerPortfolioComponent.builder()
            .appComponent(injector)
            .build()
            .portfolioCoinListViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBarTitle(com.charliechristensen.cryptotracker.cryptotracker.R.string.portfolio)

        val adapter = PortfolioAdapter { index ->
            viewModel.inputs.onClickItem(index)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.outputs.getPortfolioValue()
            .bind { walletTotalValueTextView.text = it }

        viewModel.outputs.getPortfolioValueChange()
            .bind {
                portfolio24HourValueChangeTextView.text = it.value
                activity?.getColorFromResource(ColorUtils.getColorInt(it.color))?.let { colorInt ->
                    portfolio24HourValueChangeTextView.setTextColor(colorInt)
                }
            }

        viewModel.outputs.getPortfolioPercentChange24Hour()
            .bind {
                portfolio24HourChangeTextView.text = it.value
                activity?.getColorFromResource(ColorUtils.getColorInt(it.color))?.let { colorInt ->
                    portfolio24HourChangeTextView.setTextColor(colorInt)
                }
            }

        viewModel.outputs.coinList()
            .bind { adapter.submitList(it) }

        viewModel.outputs.showNetworkError()
            .bind { showToast(com.charliechristensen.cryptotracker.cryptotracker.R.string.error_network_error) }

        viewModel.outputs.showCoinDetailController()
            .bind { pushCoinDetailController(it) }

        viewModel.outputs.showChooseCoinsListController()
            .bind { pushChooseCoinsListController() }
    }

    private fun pushCoinDetailController(symbol: String) {
        findNavController().navigate(NavigationHelper.coinDetailUri(symbol))
    }

    private fun pushChooseCoinsListController() {
        findNavController().navigate(NavigationHelper.coinListUri(true))
    }

}

