package com.charliechristensen.cryptotracker.common.lists

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

abstract class BasePagedListAdapter<T : ListItem> :
    PagedListAdapter<T, BaseViewHolder<T>>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
            oldItem.areItemsTheSame(newItem)

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
            oldItem.areContentsTheSame(newItem)
    }) {

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val listItem = getItem(position) ?: return
        holder.bind(listItem)
    }

    override fun getItemViewType(position: Int): Int = getItem(position)?.viewType ?: -1

}