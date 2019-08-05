package com.charliechristensen.cryptotracker.common.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo

abstract class BaseActivity : AppCompatActivity() {

    protected val disposables = CompositeDisposable()

    protected abstract val layoutResource: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResource)
    }

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