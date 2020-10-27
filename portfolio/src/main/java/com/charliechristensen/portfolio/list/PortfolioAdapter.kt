package com.charliechristensen.portfolio.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseListAdapter
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.R
import com.charliechristensen.portfolio.databinding.CellAddCoinBinding
import com.charliechristensen.portfolio.databinding.CellCoinPortfolioBinding

class PortfolioAdapter(private val onClickItemCallback: PortfolioAdapterCallback) : BaseListAdapter<PortfolioListItem>() {

    interface PortfolioAdapterCallback {
        fun onClickItem(listItem: PortfolioListItem)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<PortfolioListItem> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.cell_coin_portfolio -> PortfolioCoinCell(CellCoinPortfolioBinding.inflate(layoutInflater, parent, false), onClickItemCallback)
            R.layout.cell_add_coin -> AddCoinCell(CellAddCoinBinding.inflate(layoutInflater, parent, false), onClickItemCallback)
            else -> error("No view for viewType:$viewType exists for ${PortfolioAdapter::class.java.simpleName}")
        }
    }
}
