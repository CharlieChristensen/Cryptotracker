package com.charliechristensen.cryptotracker.common

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Chuck on 1/15/2018.
 */
abstract class BaseViewModel: ViewModel(){
    protected val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}