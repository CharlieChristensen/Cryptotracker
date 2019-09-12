package com.charliechristensen.cryptotracker.common.ui

import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.charliechristensen.cryptotracker.common.BaseViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo

abstract class BaseFragment<VM : BaseViewModel>(@LayoutRes contentLayoutId: Int): Fragment(contentLayoutId) {

    private val disposables = CompositeDisposable()

    protected abstract val viewModel: VM

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    protected fun setActionBarTitle(@StringRes resId: Int) {
        if (activity is AppCompatActivity) {
            val appCompatActivity = activity as AppCompatActivity?
            val actionBar = appCompatActivity?.supportActionBar
            actionBar?.setTitle(resId)
        }
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