package com.alexz.messenger.app.data.model.result

/**
 * Implementation of [Result.ISuccess].
 * */
class Success<T>(override val value: T?) : Result<T>(), Result.ISuccess<T>{

    override fun toString(): String {
        return "Success(value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Success<*>) return false

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value?.hashCode() ?: 0
    }
}
