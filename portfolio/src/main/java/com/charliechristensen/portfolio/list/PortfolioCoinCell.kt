package com.charliechristensen.portfolio.list

import coil.load
import com.charliechristensen.cryptotracker.common.extensions.setColorAttribute
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import com.charliechristensen.portfolio.databinding.CellCoinPortfolioBinding

class PortfolioCoinCell(
    private val binding: CellCoinPortfolioBinding,
    private val callback: PortfolioAdapter.PortfolioAdapterCallback
) : BaseViewHolder<PortfolioListItem>(binding.root) {

    override fun bind(listItem: PortfolioListItem) {
        if (listItem is PortfolioListItem.Coin) {
            binding.apply {
                root.setOnClickListener { callback.onClickItem(listItem) }
                logoImageView.load(listItem.imageUri)
                coinSymbolTextView.text = listItem.symbol
                currentPriceTextView.text = listItem.currentPrice
                amountChangeTextView.text = listItem.priceChange.value
                amountChangeTextView.setColorAttribute(listItem.priceChange.color)
                walletValueTextView.text = listItem.walletValue
                walletValueChangeTextView.text = listItem.walletValueChange.value
                walletValueChangeTextView.setColorAttribute(listItem.walletValueChange.color)
            }
        }
    }
}
