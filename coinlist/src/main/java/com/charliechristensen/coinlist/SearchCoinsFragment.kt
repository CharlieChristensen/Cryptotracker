package com.charliechristensen.coinlist

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.charliechristensen.coinlist.di.DaggerCoinListComponent
import com.charliechristensen.coinlist.list.SearchCoinsAdapter
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.savedStateViewModel
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import kotlinx.android.synthetic.main.view_search_coins.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import ru.ldralighieri.corbind.appcompat.queryTextChanges
import ru.ldralighieri.corbind.view.clicks

@FlowPreview
@ExperimentalCoroutinesApi
class SearchCoinsFragment : BaseFragment<SearchCoinsViewModel.ViewModel>(R.layout.view_search_coins),
    SearchCoinsAdapter.SearchCoinAdapterCallback {

    override val viewModel: SearchCoinsViewModel.ViewModel by savedStateViewModel { savedStateHandle ->
        val fragmentArgs = SearchCoinsFragmentArgs.fromBundle(requireArguments())
        DaggerCoinListComponent.builder()
            .appComponent(injector)
            .build()
            .searchCoinsViewModelFactory.create(fragmentArgs.filterOwnedCoins, savedStateHandle)
    }

    //region Lifecycle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle(com.charliechristensen.cryptotracker.cryptotracker.R.string.coins)

        val adapter = SearchCoinsAdapter(this)
        coinsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchView.clicks()
            .bind { searchView.isIconified = false }

        searchView.queryTextChanges()
            .bind { viewModel.inputs.setSearchQuery(it) }

        viewModel.outputs.coinList
            .bind {
                adapter.submitList(it)
                if (coinsRecyclerView.adapter == null) { // Attaching the adapter here allows automatic state restore to work properly with async data
                    coinsRecyclerView.adapter = adapter
                }
            }

        viewModel.outputs.showNetworkError
            .bind { showToast(com.charliechristensen.cryptotracker.cryptotracker.R.string.error_network_error) }
    }

    //endregion

    //region SearchCoinsAdapterCallback

    override fun onClickCoin(symbol: String) {
        viewModel.inputs.onClickCoin(symbol)
    }

    override fun onClickRefresh() {
        viewModel.inputs.onClickRefresh()
    }

    //endregion

}
