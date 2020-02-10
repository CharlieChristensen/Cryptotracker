package com.charliechristensen.portfolio

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.extensions.viewModel
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.portfolio.databinding.ViewPortfolioCoinListBinding
import com.charliechristensen.portfolio.di.DaggerPortfolioComponent
import com.charliechristensen.portfolio.list.PortfolioAdapter
import com.charliechristensen.portfolio.list.PortfolioListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PortfolioFragment :
    BaseFragment<PortfolioCoinListViewModel.ViewModel>(R.layout.view_portfolio_coin_list),
    PortfolioAdapter.PortfolioAdapterCallback {

    override val viewModel: PortfolioCoinListViewModel.ViewModel by viewModel {
        DaggerPortfolioComponent.factory()
            .create(injector)
            .portfolioCoinListViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActionBarTitle(com.charliechristensen.cryptotracker.cryptotracker.R.string.portfolio)

        val binding: ViewPortfolioCoinListBinding = ViewPortfolioCoinListBinding.bind(view)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = PortfolioAdapter(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.outputs.coinList
            .bind {
                adapter.submitList(it)
                if(binding.recyclerView.adapter == null) {
                    binding.recyclerView.adapter = adapter
                }
            }

        viewModel.outputs.showNetworkError
            .bind { showToast(com.charliechristensen.cryptotracker.cryptotracker.R.string.error_network_error) }
    }

    override fun onClickItem(listItem: PortfolioListItem) {
        viewModel.inputs.onClickItem(listItem)
    }

}
