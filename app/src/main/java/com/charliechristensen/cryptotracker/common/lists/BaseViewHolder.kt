package com.charliechristensen.cryptotracker.common.lists

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(view: View) :
    RecyclerView.ViewHolder(view) {

    open fun bind(listItem: T) {}

}
