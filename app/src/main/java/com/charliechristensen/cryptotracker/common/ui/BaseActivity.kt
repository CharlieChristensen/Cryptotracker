package com.charliechristensen.cryptotracker.common.ui

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    inline fun <T> Flow<T>.bind(crossinline action: suspend (value: T) -> Unit) =
        this.onEach { action(it) }
            .launchIn(lifecycleScope)

}
