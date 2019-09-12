package com.charliechristensen.cryptotracker.common.ui

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo

abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    private val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    private val onNextStub: (Any) -> Unit = {}
    private val onCompleteStub: () -> Unit = {}
    private val onErrorStub: (Throwable) -> Unit = {
        RxJavaPlugins.onError(
            OnErrorNotImplementedException(it)
        )
    }

    fun <T : Any> Observable<T>.bind(
        onError: (Throwable) -> Unit = onErrorStub,
        onComplete: () -> Unit = onCompleteStub,
        onNext: (T) -> Unit = onNextStub
    ) {
        this.observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext, onError, onComplete)
            .addTo(disposables)
    }

}