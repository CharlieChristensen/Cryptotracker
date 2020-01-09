package com.charliechristensen.coinlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.lists.BaseListAdapter
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsAdapter(private val callback: SearchCoinAdapterCallback) : BaseListAdapter<SearchCoinsListItem>() {

    interface SearchCoinAdapterCallback {
        fun onClickCoin(symbol: String)
        fun onClickRefresh()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<SearchCoinsListItem> = when (viewType) {
        R.layout.cell_coin_list -> SearchCoinsCell(parent, callback)
        R.layout.view_loading_header -> SearchCoinsLoadingCell(parent)
        R.layout.cell_coin_list_footer -> SearchCoinsRefreshCell(parent, callback)
        else -> error("No view for viewType:$viewType exists for ${SearchCoinsAdapter::class.java.simpleName}")
    }
}
