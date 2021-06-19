package com.alexz.messenger.app.ui.viewmodels

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexz.messenger.app.data.Data
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class DataViewModel<T> : ViewModel(), IDataViewModel<T>{

    override val data : LiveData<Data<T>>
        get() = mutableData

    private val mutableData = MutableLiveData<Data<T>>()
    private var disposable : Disposable?= null

    protected fun observe(observable: Observable<T>) {
        disposable?.dispose()
        disposable = observable
                .subscribe(
                        { mutableData.postValue(Data(value = it)) },
                        { mutableData.postValue((Data(error = it))) }
                )
    }


    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}