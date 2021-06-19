package com.alexz.messenger.app.ui.viewmodels

import androidx.lifecycle.LiveData
import com.alexz.messenger.app.data.Data

interface IDataViewModel<T> {
    val data : LiveData<Data<T>>
}