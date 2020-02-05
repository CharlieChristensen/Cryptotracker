package com.charliechristensen.coinlist

import android.os.Bundle
import android.view.View
import com.charliechristensen.coinlist.databinding.ViewSearchCoinsBinding
import com.charliechristensen.coinlist.di.DaggerCoinListComponent
import com.charliechristensen.coinlist.list.SearchCoinsAdapter
import com.charliechristensen.coinlist.list.SearchCoinsPagedAdapter
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.savedStateViewModel
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import ru.ldralighieri.corbind.appcompat.queryTextChanges
import ru.ldralighieri.corbind.view.clicks
import timber.log.Timber

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

        val binding = ViewSearchCoinsBinding.bind(view)
        binding.lifecycleOwner = this

        val pagedAdapter = SearchCoinsPagedAdapter(this)

        binding.searchView.clicks()
            .bind { binding.searchView.isIconified = false }

        binding.searchView.queryTextChanges()
            .drop(1)
            .debounce(400)
            .bind { viewModel.inputs.setSearchQuery(it) }

        viewModel.outputs.coinList
            .bind { listItems ->
                Timber.d(listItems.size.toString())
                pagedAdapter.submitList(listItems)
                if (binding.coinsRecyclerView.adapter == null) {
                    binding.coinsRecyclerView.adapter = pagedAdapter
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
