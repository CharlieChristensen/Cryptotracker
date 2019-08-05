package com.charliechristensen.cryptotracker.cryptotracker.portfolio.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.lists.BaseListAdapter
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.cryptotracker.cryptotracker.R

class PortfolioAdapter(private val onClickItemCallback: (Int) -> Unit): BaseListAdapter<PortfolioListItem>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<PortfolioListItem> {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType){
            R.layout.cell_coin_portfolio -> PortfolioCoinCell(inflater, parent, onClickItemCallback)
            R.layout.cell_add_coin -> AddCoinCell(inflater, parent, onClickItemCallback)
            else -> error("No view for viewType:$viewType exists for ${PortfolioAdapter::class.java.simpleName}")
        }
    }

}
