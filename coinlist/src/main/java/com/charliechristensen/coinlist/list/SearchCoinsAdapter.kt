package com.charliechristensen.coinlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.coinlist.R
import com.charliechristensen.coinlist.databinding.CellCoinListBinding
import com.charliechristensen.coinlist.databinding.CellCoinListFooterBinding
import com.charliechristensen.coinlist.databinding.ViewLoadingHeaderBinding
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
    ): BaseViewHolder<SearchCoinsListItem> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.cell_coin_list -> SearchCoinsCell(CellCoinListBinding.inflate(layoutInflater, parent, false), callback)
            R.layout.view_loading_header -> SearchCoinsLoadingCell(ViewLoadingHeaderBinding.inflate(layoutInflater, parent, false))
            R.layout.cell_coin_list_footer -> SearchCoinsRefreshCell(CellCoinListFooterBinding.inflate(layoutInflater, parent, false), callback)
            else -> error("No view for viewType:$viewType exists for ${SearchCoinsAdapter::class.java.simpleName}")
        }
    }
}
