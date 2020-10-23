package com.charliechristensen.portfolio

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.portfolio.databinding.ViewPortfolioCoinListBinding
import com.charliechristensen.portfolio.di.getPortfolioModule
import com.charliechristensen.portfolio.list.PortfolioAdapter
import com.charliechristensen.portfolio.list.PortfolioListItem
import org.koin.android.ext.android.inject
import org.koin.core.module.Module

class PortfolioFragment :
    BaseFragment<PortfolioCoinListViewModel.ViewModel, ViewPortfolioCoinListBinding>(R.layout.view_portfolio_coin_list),
    PortfolioAdapter.PortfolioAdapterCallback {

    override val koinModule: Module? = getPortfolioModule()

    override val viewModel: PortfolioCoinListViewModel.ViewModel by inject()
//    viewModel {
//        DaggerPortfolioComponent.factory()
//            .create(injector)
//            .portfolioCoinListViewModel
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBarTitle(com.charliechristensen.cryptotracker.cryptotracker.R.string.portfolio)

        binding.viewModel = viewModel

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
    }

    override fun onClickItem(listItem: PortfolioListItem) {
        viewModel.inputs.onClickItem(listItem)
    }

}
