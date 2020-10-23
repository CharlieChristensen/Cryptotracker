package com.charliechristensen.coinlist

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charliechristensen.coinlist.databinding.ViewSearchCoinsBinding
import com.charliechristensen.coinlist.di.getCoinListModule
import com.charliechristensen.coinlist.list.SearchCoinsAdapter
import com.charliechristensen.coinlist.list.SearchCoinsPagedAdapter
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.module.Module
import ru.ldralighieri.corbind.appcompat.queryTextChanges
import ru.ldralighieri.corbind.view.clicks


class SearchCoinsFragment :
    BaseFragment<SearchCoinsViewModel.ViewModel, ViewSearchCoinsBinding>(R.layout.view_search_coins),
    SearchCoinsAdapter.SearchCoinAdapterCallback {

    override val koinModule: Module = getCoinListModule(
        SearchCoinsFragmentArgs.fromBundle(requireArguments()).filterOwnedCoins
    )

    override val viewModel: SearchCoinsViewModel.ViewModel by viewModel()
//    savedStateViewModel { savedStateHandle ->
//        val fragmentArgs = SearchCoinsFragmentArgs.fromBundle(requireArguments())
//        DaggerCoinListComponent.factory()
//            .create(injector)
//            .searchCoinsViewModelFactory.create(fragmentArgs.filterOwnedCoins, savedStateHandle)
//    }

    //region Lifecycle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle(com.charliechristensen.cryptotracker.cryptotracker.R.string.coins)

        val pagedAdapter = SearchCoinsPagedAdapter(this).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.coinsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagedAdapter
        }
        binding.searchView.clicks()
            .bind { binding.searchView.isIconified = false }

        binding.searchView.queryTextChanges()
            .drop(1)
            .debounce(400)
            .bind { viewModel.inputs.setSearchQuery(it) }

        viewModel.outputs.coinList
            .bind(pagedAdapter::submitList)

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
