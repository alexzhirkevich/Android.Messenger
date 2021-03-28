package com.alexz.messenger.app.data.model.result

import androidx.annotation.StringRes

/**
 * Base class for present and future result-returning actions
 * */
abstract class Result<T>{

    /**
     * Successful result
     * */
    interface ISuccess<T> {
        val value : T?
    }

    /**
     * Failure result
     * */
    interface IError {
        @get:StringRes
        val error: Int
    }

    /**
     * Mutable observable future result
     * Can be notified only by [ISuccess] of [IError]
     * */
    interface IMutableFuture<T> {
        /**
         * Notify listeners in UI-thread
         *
         * @param result - [ISuccess] or [IError].
         * Future cannot call other future.
         * @see ResultListener
         * */
        fun set(result: Result<T>)
        /**
         * Notify listeners in current thread
         *
         * @param result - [ISuccess] or [IError].
         * Future cannot be result of future.
         * @see ResultListener
         * */
        fun post(result: Result<T>)
        /**
         * Notify listeners about progress. Calls in UI-thread.
         *
         *
         * @param progress - relative value of result progress
         * @see ResultListener
         * @see IFuture.hasProgress
         * */
        fun setProgress(progress: Double)
    }

    /**
     * Observable future result
     * @see ResultListener
     * */
    interface IFuture<T> {
        /**
         * Shows if the action has progress.
         * [IMutableFuture.setProgress] used to update progress
         * */
        fun addResultListener(listener: ResultListener<T>): Boolean
        fun removeResultListener(listener: ResultListener<T>?): Boolean
        fun clearResultListeners()
    }
}