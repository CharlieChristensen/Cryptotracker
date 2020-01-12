package com.charliechristensen.coinlist.list

import android.view.ViewGroup
import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsRefreshCell(
    parent: ViewGroup,
    callback: SearchCoinsAdapter.SearchCoinAdapterCallback
) : BaseViewHolder<SearchCoinsListItem>(
    inflateView(R.layout.cell_coin_list_footer, parent)
) {
    init {
        itemView.setOnClickListener { callback.onClickRefresh() }
    }
}
