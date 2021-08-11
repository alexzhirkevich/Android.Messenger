package com.community.messenger.app.data

data class Data<T>(override val value: T? = null,override val error: Throwable? = null) : IData<T>