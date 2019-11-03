package com.charliechristensen.cryptotracker.common.ui

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.charliechristensen.cryptotracker.common.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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

    inline fun <T> LiveData<T>.bind(crossinline observer: (T) -> Unit) {
        this.observe(this@BaseFragment, Observer { observer(it) })
    }

    protected inline fun <T> Flow<T>.bind(crossinline action: suspend (value: T) -> Unit): Job {
        return lifecycleScope.launchWhenStarted {
            this@bind
                .flowOn(Dispatchers.Main.immediate)
                .collect { action(it) }
        }
    }

    protected fun <T> Flow<T>.launch(): Job = lifecycleScope.launch {
        whenStarted {
            collect() // tail-call
        }
    }

}
