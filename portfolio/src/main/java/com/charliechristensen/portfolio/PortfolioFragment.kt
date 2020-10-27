package com.charliechristensen.portfolio

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.cryptotracker.common.ui.viewBinding
import com.charliechristensen.portfolio.databinding.ViewPortfolioCoinListBinding
import com.charliechristensen.portfolio.di.portfolioModule
import com.charliechristensen.portfolio.list.PortfolioAdapter
import com.charliechristensen.portfolio.list.PortfolioListItem
import com.charliechristensen.cryptotracker.common.extensions.setText
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.module.Module

class PortfolioFragment : BaseFragment(R.layout.view_portfolio_coin_list),
    PortfolioAdapter.PortfolioAdapterCallback {

    override val koinModule: Module by lazy { portfolioModule }

    private val viewModel: PortfolioCoinListViewModel.ViewModel by viewModel()

    private val binding: ViewPortfolioCoinListBinding by viewBinding(ViewPortfolioCoinListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBarTitle(com.charliechristensen.cryptotracker.cryptotracker.R.string.portfolio)

        val portfolioAdapter = PortfolioAdapter(this).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.recyclerView.apply {
            adapter = portfolioAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
        }
        viewModel.outputs.coinList
            .bind(portfolioAdapter::submitList)

        viewModel.outputs.showNetworkError
            .bind { showToast(com.charliechristensen.cryptotracker.cryptotracker.R.string.error_network_error) }

        viewModel.outputs.walletTotalValue
            .bind(binding.walletTotalValueTextView::setText)

        viewModel.outputs.percentChange24Hour
            .bind(binding.portfolio24HourChangeTextView::setText)

        viewModel.outputs.portfolioValueChange
            .bind(binding.portfolio24HourValueChangeTextView::setText)
    }

    override fun onClickItem(listItem: PortfolioListItem) {
        viewModel.inputs.onClickItem(listItem)
    }

}
