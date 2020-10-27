package com.charliechristensen.cryptotracker.common.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected abstract val koinModule: Module

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(koinModule)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(koinModule)
    }

    protected fun setActionBarTitle(@StringRes resId: Int) {
        if (activity is AppCompatActivity) {
            val appCompatActivity = activity as AppCompatActivity?
            val actionBar = appCompatActivity?.supportActionBar
            actionBar?.setTitle(resId)
        }
    }

    protected inline fun <T> Flow<T>.bind(crossinline observer: suspend (T) -> Unit) {
        onEach { observer(it) }
            .launchIn(this@BaseFragment.viewLifecycleOwner.lifecycleScope)
    }
}
