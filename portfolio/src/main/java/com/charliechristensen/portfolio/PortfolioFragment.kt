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

        viewModel.outputs.portfolioState
            .bind { renderViewState(it) }

        viewModel.outputs.showNetworkError
            .bind { showToast(com.charliechristensen.cryptotracker.cryptotracker.R.string.error_network_error) }

        viewModel.outputs.showCoinDetailController
            .bind { pushCoinDetailController(it) }

        viewModel.outputs.showChooseCoinsListController
            .bind { pushChooseCoinsListController() }

    }

    private fun renderViewState(portfolioState: PortfolioListData) {

        getAdapter().submitList(portfolioState.coinList)

        walletTotalValueTextView.text = portfolioState.formattedValue

        val valueChange = portfolioState.portfolioValueChange
        portfolio24HourValueChangeTextView.text = valueChange.value
        activity?.getColorFromResource(ColorUtils.getColorInt(valueChange.color))
            ?.let { colorInt ->
                portfolio24HourValueChangeTextView.setTextColor(colorInt)
            }

        val percentChanged = portfolioState.percentChange24Hour
        portfolio24HourChangeTextView.text = percentChanged.value
        activity?.getColorFromResource(ColorUtils.getColorInt(percentChanged.color))
            ?.let { colorInt ->
                portfolio24HourChangeTextView.setTextColor(colorInt)
            }

    }

    private fun getAdapter(): PortfolioAdapter =
        recyclerView.adapter as? PortfolioAdapter?
            ?: (PortfolioAdapter { item ->
                viewModel.inputs.onClickItem(item)
            }.apply {
                recyclerView.adapter = this
                recyclerView.layoutManager = LinearLayoutManager(context)
            })

    private fun pushCoinDetailController(symbol: String) {
        findNavController().navigate(NavigationHelper.coinDetailUri(symbol))
    }

    private fun pushChooseCoinsListController() {
        findNavController().navigate(NavigationHelper.coinListUri(true))
    }

}

