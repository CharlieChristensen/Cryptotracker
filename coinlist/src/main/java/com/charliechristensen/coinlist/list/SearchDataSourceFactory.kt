package com.charliechristensen.coinlist.list

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.charliechristensen.coinlist.SearchCoinsInteractor

class SearchDataSourceFactory constructor(
    private val interactor: SearchCoinsInteractor,
    private val filterOutOwnedCoins: Boolean
): DataSource.Factory<Int, SearchCoinsListItem>() {

    private val dataSourceLiveData = MutableLiveData<SearchDataSource>()
    private var query = ""

    override fun create(): DataSource<Int, SearchCoinsListItem> {
        val dataSource = SearchDataSource(interactor, query, filterOutOwnedCoins)
        dataSourceLiveData.postValue(dataSource)
        return dataSource
    }

    fun setSearchTerm(searchTerm: String) {
        this.query = searchTerm
        dataSourceLiveData.value?.invalidate()
    }

    fun refresh() {
        dataSourceLiveData.value?.invalidate()
    }

}
