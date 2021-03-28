package com.alexz.messenger.app.data.model.result

import androidx.annotation.UiThread

/**
 * Listener for [Result.IFuture]
 * */
interface ResultListener<T> {

    /**
     * @see Result.IMutableFuture.set]
     * @see Result.IMutableFuture.post]
     * */
    @UiThread
    fun onSuccess(result: Result.ISuccess<T>)

    /**
     * @see Result.IMutableFuture.set]
     * @see Result.IMutableFuture.post]
     * */
    @UiThread
    fun onError(error: Result.IError)

    /**
     * @see Result.IMutableFuture.setProgress]
     * */
    @UiThread
    fun onProgress(percent: Double?) {return}
}