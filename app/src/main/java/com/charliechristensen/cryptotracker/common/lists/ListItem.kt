package com.charliechristensen.cryptotracker.common.lists

interface ListItem {
    val viewType: Int
    fun areItemsTheSame(otherItem: ListItem): Boolean
    fun areContentsTheSame(otherItem: ListItem): Boolean
}