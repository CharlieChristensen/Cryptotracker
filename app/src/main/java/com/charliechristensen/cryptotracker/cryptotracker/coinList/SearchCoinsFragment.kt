package com.charliechristensen.cryptotracker.cryptotracker.coinList

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.charliechristensen.cryptotracker.common.*
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.charliechristensen.cryptotracker.cryptotracker.R
import com.charliechristensen.cryptotracker.cryptotracker.coinDetail.CoinDetailFragment
import com.charliechristensen.cryptotracker.cryptotracker.coinList.list.SearchCoinsAdapter
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChanges
import kotlinx.android.synthetic.main.view_search_coins.*

class SearchCoinsFragment : BaseFragment<SearchCoinsViewModel.ViewModel>(),
    SearchCoinsAdapter.SearchCoinAdapterCallback {

    override val viewModel: SearchCoinsViewModel.ViewModel by savedStateViewModel(this) { savedStateHandle ->
        val filterOutOwnedCoins = arguments?.getBoolean(KEY_FILTER_OWNED_COINS, false) ?: false
        injector.searchCoinsViewModelFactory.create(filterOutOwnedCoins, savedStateHandle)
    }

    override val layoutResource: Int
        get() = R.layout.view_search_coins

    //region Lifecycle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle(R.string.coins)

        val adapter = SearchCoinsAdapter(this)
        coinsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView.setOnClickListener { searchView.isIconified = false }
        searchView.queryTextChanges()
            .bind { viewModel.inputs.setSearchQuery(it) }

        viewModel.outputs.coinList()
            .bind {
                adapter.submitList(it)
                if (coinsRecyclerView.adapter == null) { //Attaching the adapter here allows automatic state restore to work properly with async data
                    coinsRecyclerView.adapter = adapter
                }
            }

        viewModel.outputs.showCoinDetailController()
            .bind { pushCoinDetailController(it) }

        viewModel.outputs.showNetworkError()
            .bind { showToast(R.string.error_network_error) }
    }

    //endregion

    //region SearchCoinsAdapterCallback

    override fun onClickListItem(index: Int) {
        viewModel.inputs.onClickListItem(index)
    }
    //endregion

    private fun pushCoinDetailController(symbol: String) {
        pushFragment(CoinDetailFragment.newInstance(symbol))
    }

    companion object {
        private const val KEY_FILTER_OWNED_COINS = "key_filter_owned_coins"

        fun newInstance(filterOutOwnedCoins: Boolean): SearchCoinsFragment = fragment {
            putBoolean(KEY_FILTER_OWNED_COINS, filterOutOwnedCoins)
        }
    }

}
