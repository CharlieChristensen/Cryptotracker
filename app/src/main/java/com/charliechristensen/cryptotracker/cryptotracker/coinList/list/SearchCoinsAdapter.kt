package com.charliechristensen.cryptotracker.cryptotracker.coinList.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseListAdapter
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.cryptotracker.cryptotracker.R

class SearchCoinsAdapter(private val callback: SearchCoinAdapterCallback) : BaseListAdapter<SearchCoinsListItem>() {

    interface SearchCoinAdapterCallback {
        fun onClickListItem(index: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<SearchCoinsListItem> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.cell_coin_list -> SearchCoinsCell(inflater, parent, callback)
            R.layout.view_loading_header -> SearchCoinsLoadingCell(inflater, parent)
            R.layout.cell_coin_list_footer -> SearchCoinsRefreshCell(inflater, parent, callback)
            else -> error("No view for viewType:$viewType exists for ${SearchCoinsAdapter::class.java.simpleName}")
        }
    }

}