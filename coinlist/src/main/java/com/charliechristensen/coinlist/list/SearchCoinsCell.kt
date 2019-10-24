package com.charliechristensen.coinlist.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.charliechristensen.coinlist.R
import com.charliechristensen.cryptotracker.common.GlideApp
import com.charliechristensen.cryptotracker.common.lists.BaseViewHolder
import kotlinx.android.synthetic.main.cell_coin_list.*

class SearchCoinsCell(
    inflater: LayoutInflater,
    parent: ViewGroup,
    callback: SearchCoinsAdapter.SearchCoinAdapterCallback
) : BaseViewHolder<SearchCoinsListItem>(
    inflater.inflate(R.layout.cell_coin_list, parent, false)
) {

    init {
        itemView.setOnClickListener {
            callback.onClickListItem(adapterPosition)
        }
    }

    override fun bind(listItem: SearchCoinsListItem) {
        if(listItem is SearchCoinsListItem.Coin){
            coinNameTextView.text = listItem.name
            coinSymbolTextView.text = listItem.symbol
            GlideApp.with(logoImageView)
                .load(listItem.imageUri)
                .apply(RequestOptions.circleCropTransform())
                .into(logoImageView)
        }
    }

}