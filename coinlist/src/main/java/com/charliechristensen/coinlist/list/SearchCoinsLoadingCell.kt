package com.charliechristensen.coinlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsLoadingCell(
    inflater: LayoutInflater,
    parent: ViewGroup
) : BaseViewHolder<SearchCoinsListItem>(
    inflater.inflate(R.layout.view_loading_header, parent, false)
)
