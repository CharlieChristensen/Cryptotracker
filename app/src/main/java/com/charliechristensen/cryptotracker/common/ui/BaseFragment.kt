package com.charliechristensen.cryptotracker.common.ui

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.charliechristensen.cryptotracker.common.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
abstract class BaseFragment<VM : BaseViewModel>(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {

    protected abstract val viewModel: VM

    protected fun setActionBarTitle(@StringRes resId: Int) {
        if (activity is AppCompatActivity) {
            val appCompatActivity = activity as AppCompatActivity?
            val actionBar = appCompatActivity?.supportActionBar
            actionBar?.setTitle(resId)
        }
    }

    fun <T> LiveData<T>.bind(observer: (T) -> Unit) {
        this.observe(this@BaseFragment, Observer { observer(it) })
    }

    inline fun <T> Flow<T>.bind(crossinline action: suspend (value: T) -> Unit) =
        this.onEach { action(it) }
            .launchIn(lifecycleScope)

}
