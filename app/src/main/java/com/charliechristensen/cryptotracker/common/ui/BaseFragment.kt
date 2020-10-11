package com.charliechristensen.cryptotracker.common.ui

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import com.charliechristensen.cryptotracker.common.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseFragment<VM : BaseViewModel, B: ViewDataBinding>(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {

    protected abstract val viewModel: VM
    protected val binding: B by viewBinding {
        val binding: B = DataBindingUtil.bind(requireView())!!
        binding.lifecycleOwner = this
        binding
    }

    protected fun setActionBarTitle(@StringRes resId: Int) {
        if (activity is AppCompatActivity) {
            val appCompatActivity = activity as AppCompatActivity?
            val actionBar = appCompatActivity?.supportActionBar
            actionBar?.setTitle(resId)
        }
    }

    inline fun <T> LiveData<T>.bind(crossinline observer: (T) -> Unit) {
        this.observe(viewLifecycleOwner, Observer { observer(it) })
    }

    protected inline fun <T> Flow<T>.bind(crossinline observer: suspend (T) -> Unit) {
        this.onEach { observer(it) }
            .launchIn(this@BaseFragment.viewLifecycleOwner.lifecycle.coroutineScope)
    }
}
