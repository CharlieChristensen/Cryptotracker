package com.charliechristensen.cryptotracker.common.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData

abstract class BaseActivity : AppCompatActivity() {

    inline fun <T> LiveData<T>.bind(crossinline observer: (T) -> Unit) {
        this.observe(this@BaseActivity, { observer(it) })
    }

}
