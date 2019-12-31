package com.charliechristensen.cryptotracker.common.ui

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    inline fun <T> LiveData<T>.bind(crossinline observer: (T) -> Unit) {
        this.observe(this@BaseActivity, Observer { observer(it) })
    }
}
