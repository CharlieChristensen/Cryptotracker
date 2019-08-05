package com.charliechristensen.cryptotracker.cryptotracker.coinList.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.cryptotracker.cryptotracker.R

class SearchCoinsLoadingCell(
    inflater: LayoutInflater,
    parent: ViewGroup
) : BaseViewHolder<SearchCoinsListItem>(
    inflater.inflate(R.layout.view_loading_header, parent, false)
)