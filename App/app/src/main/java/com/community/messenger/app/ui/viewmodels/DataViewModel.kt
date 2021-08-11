package com.community.messenger.app.ui.viewmodels

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.messenger.app.data.Data
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class DataViewModel<T> : ViewModel(), IDataViewModel<T>{

    override val data : LiveData<Data<T>>
        get() = mutableData

    private val mutableData = MutableLiveData<Data<T>>()
    private var disposable : Disposable?= null
    private var job : Job?= null

    protected fun collect(flow : Flow<T>){
        job?.cancel()
        job = viewModelScope.launch {
            try {
                flow.collect {
                    mutableData.postValue(Data(value = it))
                }
            }catch (t : Throwable){
                mutableData.postValue(Data(error = t))
            }
        }
    }

//    protected fun observe(observable: Observable<T>) {
//        disposable?.dispose()
//        disposable = observable
//                .subscribe(
//                        {
//                            mutableData.postValue(Data(value = it)) },
//                        {
//                            mutableData.postValue((Data(error = it))) },
//                        {
//                            disposable?.dispose() }
//                )
//    }


    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}