package com.charliechristensen.coinlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsRefreshCell(
    inflater: LayoutInflater,
    parent: ViewGroup,
    callback: SearchCoinsAdapter.SearchCoinAdapterCallback
): BaseViewHolder<SearchCoinsListItem>(
    inflater.inflate(R.layout.cell_coin_list_footer, parent, false)
) {
    init {
        itemView.setOnClickListener { callback.onClickListItem(adapterPosition) }
    }
}