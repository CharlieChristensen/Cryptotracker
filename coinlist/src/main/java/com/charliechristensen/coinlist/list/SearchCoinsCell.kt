package com.charliechristensen.coinlist.list

import com.charliechristensen.coinlist.databinding.CellCoinListBinding
import com.charliechristensen.cryptotracker.common.GlideApp
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder

class SearchCoinsCell(
    private val binding: CellCoinListBinding,
    private val callback: SearchCoinsAdapter.SearchCoinAdapterCallback
) : BaseViewHolder<SearchCoinsListItem>(binding.root) {

    override fun bind(listItem: SearchCoinsListItem) {
        if (listItem is SearchCoinsListItem.Coin) {
            binding.coinNameTextView.text = listItem.name
            binding.coinSymbolTextView.text = listItem.symbol
            binding.root.setOnClickListener {
                callback.onClickCoin(listItem.symbol)
            }
            GlideApp.with(binding.logoImageView)
                .load(listItem.imageUri)
                .circleCrop()
                .into(binding.logoImageView)
        }
    }

}
