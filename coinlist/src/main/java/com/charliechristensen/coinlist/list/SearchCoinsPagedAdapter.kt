package com.charliechristensen.coinlist.list

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsPagedAdapter(private val callback: SearchCoinsAdapter.SearchCoinAdapterCallback): PagedListAdapter<SearchCoinsListItem, BaseViewHolder<SearchCoinsListItem>>(DIFF) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<SearchCoinsListItem> = when (viewType) {
        R.layout.cell_coin_list -> SearchCoinsCell(parent, callback)
        R.layout.view_loading_header -> SearchCoinsLoadingCell(parent)
        R.layout.cell_coin_list_footer -> SearchCoinsRefreshCell(parent, callback)
        else -> error("No view for viewType:$viewType exists for ${SearchCoinsAdapter::class.java.simpleName}")
    }

    override fun onBindViewHolder(holder: BaseViewHolder<SearchCoinsListItem>, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    override fun getItemViewType(position: Int): Int = getItem(position)?.viewType ?: 0

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SearchCoinsListItem>() {
            override fun areItemsTheSame(
                oldItem: SearchCoinsListItem,
                newItem: SearchCoinsListItem
            ): Boolean = oldItem.areItemsTheSame(newItem)

            override fun areContentsTheSame(
                oldItem: SearchCoinsListItem,
                newItem: SearchCoinsListItem
            ): Boolean = oldItem.areContentsTheSame(newItem)
        }
    }
}
