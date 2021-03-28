package com.alexz.messenger.app.data.model.result

import androidx.annotation.StringRes

/**
 * Implementation of [Result.IError].
 * */
class Error<T>(@param:StringRes override val error: Int) : Result<T>(), Result.IError{

    override fun toString(): String {
        return "Error($error)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Error<*>) return false

        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        return error
    }
}