package com.alexz.messenger.app.data

interface IData<T>{
    val value : T?
    val error : Throwable?
}