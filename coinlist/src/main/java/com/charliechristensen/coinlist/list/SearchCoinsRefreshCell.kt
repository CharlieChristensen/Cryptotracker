package com.charliechristensen.coinlist.list

import com.charliechristensen.coinlist.databinding.CellCoinListFooterBinding
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsRefreshCell(
    binding: CellCoinListFooterBinding,
    callback: SearchCoinsAdapter.SearchCoinAdapterCallback
) : BaseViewHolder<SearchCoinsListItem>(binding.root) {

    init {
        binding.root.setOnClickListener { callback.onClickRefresh() }
    }

}
