package com.charliechristensen.coinlist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.charliechristensen.coinlist.di.DaggerCoinListComponent
import com.charliechristensen.coinlist.list.SearchCoinsAdapter
import com.charliechristensen.cryptotracker.common.extensions.injector
import com.charliechristensen.cryptotracker.common.extensions.navigateRight
import com.charliechristensen.cryptotracker.common.extensions.savedStateViewModel
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.navigation.NavigationHelper
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import kotlinx.android.synthetic.main.view_search_coins.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.appcompat.queryTextChanges

@ExperimentalCoroutinesApi
@FlowPreview
class SearchCoinsFragment : BaseFragment<SearchCoinsViewModel.ViewModel>(R.layout.view_search_coins),
    SearchCoinsAdapter.SearchCoinAdapterCallback {

    private val fragmentArgs: SearchCoinsFragmentArgs by navArgs()

    override val viewModel: SearchCoinsViewModel.ViewModel by savedStateViewModel { savedStateHandle ->
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

        searchView.setOnClickListener { searchView.isIconified = false }
        searchView.queryTextChanges()
            .onEach { viewModel.inputs.setSearchQuery(it) }
            .launchIn(viewBindingScope)

        viewModel.outputs.coinList
            .bind {
                adapter.submitList(it)
                if (coinsRecyclerView.adapter == null) { //Attaching the adapter here allows automatic state restore to work properly with async data
                    coinsRecyclerView.adapter = adapter
                }
            }

        viewModel.outputs.showCoinDetailController
            .bind { pushCoinDetailController(it) }

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

    private fun pushCoinDetailController(symbol: String) {
        findNavController().navigateRight(NavigationHelper.coinDetailUri(symbol))
    }

}
