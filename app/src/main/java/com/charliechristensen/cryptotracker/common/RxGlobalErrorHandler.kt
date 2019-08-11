package com.charliechristensen.cryptotracker.common

import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import java.io.IOException
import java.net.SocketException

/**
 * Adapted from https://github.com/ReactiveX/RxJava/wiki/What%27s-different-in-2.0#error-handling
 */
class RxGlobalErrorHandler : Consumer<Throwable> {
    override fun accept(throwable: Throwable) {
        var error = throwable
        if (error is UndeliverableException && error.cause != null) {
            error = error.cause!!
        }
        if ((error is IOException) || (error is SocketException)) {
            // fine, irrelevant network problem or API that throws on cancellation
            return
        }
        if (error is InterruptedException) {
            // fine, some blocking code was interrupted by a dispose call
            return
        }
        if ((error is NullPointerException) || (error is IllegalArgumentException)) {
            // that's likely a bug in the application
            Thread.currentThread()
                .uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), error)
            return
        }
        if (error is IllegalStateException) {
            // that's a bug in RxJava or in a custom operator
            Thread.currentThread()
                .uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), error)
            return
        }
        Log.w("RxError", "Undeliverable exception received, not sure what to do $error")
    }
}