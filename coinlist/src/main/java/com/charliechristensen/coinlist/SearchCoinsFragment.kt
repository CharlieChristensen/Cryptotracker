package com.charliechristensen.coinlist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charliechristensen.coinlist.databinding.ViewSearchCoinsBinding
import com.charliechristensen.coinlist.di.getCoinListModule
import com.charliechristensen.coinlist.list.SearchCoinsAdapter
import com.charliechristensen.coinlist.list.SearchCoinsPagedAdapter
import com.charliechristensen.cryptotracker.common.extensions.showToast
import com.charliechristensen.cryptotracker.common.ui.BaseFragment
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.module.Module
import ru.ldralighieri.corbind.appcompat.queryTextChanges
import ru.ldralighieri.corbind.view.clicks


class SearchCoinsFragment : BaseFragment(R.layout.view_search_coins),
    SearchCoinsAdapter.SearchCoinAdapterCallback {

    override val koinModule: Module by lazy {
        val filterOutOwnedCoins =
            SearchCoinsFragmentArgs.fromBundle(requireArguments()).filterOwnedCoins
        getCoinListModule(
            filterOutOwnedCoins
        )
    }

    private val viewModel: SearchCoinsViewModel.ViewModel by viewModel()

    //region Lifecycle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActionBarTitle(com.charliechristensen.cryptotracker.cryptotracker.R.string.coins)

        val pagedAdapter = SearchCoinsPagedAdapter(this).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val binding: ViewSearchCoinsBinding = ViewSearchCoinsBinding.bind(view)
        binding.coinsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagedAdapter
        }
        binding.searchView.clicks()
            .bind { binding.searchView.isIconified = false }

        viewLifecycleOwner.lifecycleScope.launch {
            binding.searchView.queryTextChanges()
                .debounce(400)
                .onStart { emit("") }
                .map { it.toString() }
                .flatMapLatest(viewModel.outputs::coinList)
                .collectLatest(pagedAdapter::submitData)
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
