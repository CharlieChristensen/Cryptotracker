package com.charliechristensen.cryptotracker.common.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class BaseViewHolder<T>(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    open fun bind(listItem: T) {}

    companion object {
        fun inflateView(@LayoutRes layoutId: Int, parent: ViewGroup): View =
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    }

}
