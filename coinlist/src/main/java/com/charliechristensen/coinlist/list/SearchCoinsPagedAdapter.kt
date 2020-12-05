package com.charliechristensen.coinlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.charliechristensen.coinlist.R
import com.charliechristensen.coinlist.databinding.CellCoinListBinding
import com.charliechristensen.coinlist.databinding.CellCoinListFooterBinding
import com.charliechristensen.coinlist.databinding.ViewLoadingHeaderBinding
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsPagedAdapter(private val callback: SearchCoinsAdapter.SearchCoinAdapterCallback): PagingDataAdapter<SearchCoinsListItem, BaseViewHolder<SearchCoinsListItem>>(DIFF) {

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
