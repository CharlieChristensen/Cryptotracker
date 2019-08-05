package com.charliechristensen.cryptotracker.cryptotracker.portfolio.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.charliechristensen.cryptotracker.common.GlideApp
import com.charliechristensen.cryptotracker.common.getColorAttribute
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.cryptotracker.cryptotracker.R
import kotlinx.android.synthetic.main.cell_coin_portfolio.*

class PortfolioCoinCell(
    inflater: LayoutInflater,
    parent: ViewGroup,
    onClickItemCallback: (Int) -> Unit
) : BaseViewHolder<PortfolioListItem>(
    inflater.inflate(R.layout.cell_coin_portfolio, parent, false)
) {

    init {
        itemView.setOnClickListener {
            onClickItemCallback(adapterPosition)
        }
    }

    override fun bind(listItem: PortfolioListItem) {
        if (listItem is PortfolioListItem.Coin) {
            val context = itemView.context

            coinSymbolTextView.text = listItem.symbol
            currentPriceTextView.text = listItem.currentPrice
            walletValueTextView.text = listItem.walletValue

            val priceChange = listItem.priceChange
            amountChangeTextView.text = priceChange.value
            context.getColorAttribute(priceChange.color) { color ->
                amountChangeTextView.setTextColor(color)
            }

            val walletPriceChange = listItem.walletValueChange
            walletValueChangeTextView.text = walletPriceChange.value
            context.getColorAttribute(walletPriceChange.color) { color ->
                walletValueChangeTextView.setTextColor(color)
            }

            GlideApp.with(logoImageView)
                .load(listItem.imageUri)
                .into(logoImageView)
        }
    }

}