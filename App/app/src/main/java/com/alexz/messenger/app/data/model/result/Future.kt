package com.alexz.messenger.app.data.model.result

/**
 * Implementation of [Result.IFuture].
 * @see Result.IMutableFuture
 * @see Future
 * @see ResultListener
 * */
abstract class Future<T> : Result.IFuture<T>{

    protected val listeners:MutableSet<ResultListener<T>> = HashSet()

    override fun addResultListener(listener:ResultListener<T>):Boolean{
        return listeners.add(listener)
    }

    override fun removeResultListener(listener:ResultListener<T>?):Boolean{
        return listeners.remove(listener)
    }

    override fun clearResultListeners(){
        listeners.clear()
    }
}
