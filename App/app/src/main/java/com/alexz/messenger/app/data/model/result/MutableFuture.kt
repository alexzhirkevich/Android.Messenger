package com.alexz.messenger.app.data.model.result

import android.os.Handler
import android.os.Looper

/**
 * Implementation of [Result.IMutableFuture].
 * @see Result.IFuture
 * @see Future
 * @see ResultListener
 * */
class MutableFuture<T> : Future<T>(), Result.IMutableFuture<T> {
    private val uiHandler = Handler(Looper.getMainLooper())

    override fun post(result: Result<T>) {
        uiHandler.post { set(result) }
    }
    override fun set(result: Result<T>) {
        if (listeners.isNotEmpty()) {
            when (result) {
                is Result.ISuccess<*> -> {
                    for (listener in listeners) {
                        listener.onSuccess(result as Result.ISuccess<T>)
                    }
                }
                is Result.IError -> {
                    for (listener in listeners) {
                        listener.onError(result as Result.IError)
                    }
                }
                else -> throw IllegalArgumentException("Invalid result")
            }
        }
    }

    override fun setProgress(progress: Double) {
        if (listeners.isNotEmpty()) {
            for (listener in listeners) {
                uiHandler.post { listener.onProgress(progress) }
            }
        }
    }
}