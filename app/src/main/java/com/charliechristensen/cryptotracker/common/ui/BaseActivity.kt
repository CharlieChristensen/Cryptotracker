package com.charliechristensen.cryptotracker.common.ui

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo

/**
 * Base Activity which handles some singletons and basic functionality
 */
abstract class BaseActivity : AppCompatActivity() {

    protected val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResource())
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }

    @LayoutRes
    protected abstract fun getLayoutResource(): Int

    protected fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showToast(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
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