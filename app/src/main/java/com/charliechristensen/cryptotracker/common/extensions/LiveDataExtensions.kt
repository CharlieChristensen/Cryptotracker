package com.charliechristensen.cryptotracker.common.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

object LiveDataExtensions {

    fun <X, T, Z> combineLatest(
        first: LiveData<X>,
        second: LiveData<T>,
        combineFunction: (X, T) -> Z
    ): LiveData<Z> {
        val finalLiveData: MediatorLiveData<Z> = MediatorLiveData()

        var firstEmitted = false
        var firstValue: X? = null

        var secondEmitted = false
        var secondValue: T? = null
        finalLiveData.addSource(first) { value ->
            firstEmitted = true
            firstValue = value
            if (firstEmitted && secondEmitted) {
                finalLiveData.value = combineFunction(firstValue!!, secondValue!!)
            }
        }
        finalLiveData.addSource(second) { value ->
            secondEmitted = true
            secondValue = value
            if (firstEmitted && secondEmitted) {
                finalLiveData.value = combineFunction(firstValue!!, secondValue!!)
            }
        }
        return finalLiveData
    }
}

fun <T> LiveData<T>.skip(count: Int): LiveData<T> {
    val mutableLiveData: MediatorLiveData<T> = MediatorLiveData()
    var skippedCount = 0
    mutableLiveData.addSource(this) {
        if (skippedCount >= count) {
            mutableLiveData.value = it
        }
        skippedCount++
    }
    return mutableLiveData
}
