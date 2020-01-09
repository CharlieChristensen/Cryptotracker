package com.charliechristensen.coinlist.list

import android.view.ViewGroup
import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsLoadingCell(
    parent: ViewGroup
) : BaseViewHolder<SearchCoinsListItem>(
    inflateView(R.layout.view_loading_header, parent)
)
