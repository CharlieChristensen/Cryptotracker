package com.charliechristensen.cryptotracker.common.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseActivity : AppCompatActivity() {

    inline fun <T> LiveData<T>.bind(crossinline observer: (T) -> Unit) {
        this.observe(this@BaseActivity, { observer(it) })
    }

    protected inline fun <T> Flow<T>.bind(crossinline observer: suspend (T) -> Unit) {
        onEach {
            observer(it)
        }.launchIn(lifecycleScope)
    }

}
