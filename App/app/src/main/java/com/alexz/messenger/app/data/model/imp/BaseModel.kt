package com.alexz.messenger.app.data.model.imp

import com.alexz.messenger.app.data.model.interfaces.IBaseModel

open class BaseModel protected constructor(final override var id: String = "") : IBaseModel {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseModel) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}