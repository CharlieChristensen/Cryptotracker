package com.charliechristensen.cryptotracker.cryptotracker.portfolio

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.charliechristensen.cryptotracker.common.*
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.cryptotracker.coinDetail.CoinDetailFragment
import com.charliechristensen.cryptotracker.cryptotracker.coinList.SearchCoinsFragment
import com.charliechristensen.cryptotracker.cryptotracker.navigationDrawer.MainActivityViewModel
import com.charliechristensen.cryptotracker.cryptotracker.portfolio.list.PortfolioAdapter
import kotlinx.android.synthetic.main.view_portfolio_coin_list.*

class PortfolioFragment : BaseFragment<PortfolioCoinListViewModel.ViewModel>() {

    override val viewModel: PortfolioCoinListViewModel.ViewModel by viewModel {
        injector.portfolioCoinListViewModel
    }

    private val activityViewModel: MainActivityViewModel.ViewModel by activityViewModel {
        injector.mainActivityViewModel
    }

    override val layoutResource: Int
        get() = R.layout.view_portfolio_coin_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBarTitle(R.string.portfolio)

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
            .bind { showToast(R.string.error_network_error) }

        viewModel.outputs.showCoinDetailController()
            .bind { pushCoinDetailController(it) }

        viewModel.outputs.showChooseCoinsListController()
            .bind { pushChooseCoinsListController() }
    }

    private fun pushCoinDetailController(symbol: String) {
        pushFragment(CoinDetailFragment.newInstance(symbol))
    }

    private fun pushChooseCoinsListController() {
        pushFragment(SearchCoinsFragment.newInstance(true))
    }

    companion object {
        fun newInstance(): PortfolioFragment = fragment {}
    }

}

