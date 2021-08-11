package com.community.messenger.app.ui.viewmodels

import androidx.lifecycle.LiveData
import com.community.messenger.app.data.Data

interface IDataViewModel<T> {
    val data : LiveData<Data<T>>
}