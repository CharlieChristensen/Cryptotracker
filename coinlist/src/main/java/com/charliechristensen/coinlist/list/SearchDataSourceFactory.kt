package com.charliechristensen.coinlist.list

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.charliechristensen.coinlist.SearchCoinsInteractor
import javax.inject.Inject

class SearchDataSourceFactory @Inject constructor(
    private val interactor: SearchCoinsInteractor
): DataSource.Factory<Int, SearchCoinsListItem>() {

    private val dataSourceLiveData = MutableLiveData<SearchDataSource>()
    private var query = ""

    override fun create(): DataSource<Int, SearchCoinsListItem> {
        val dataSource = SearchDataSource(interactor, query)
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
