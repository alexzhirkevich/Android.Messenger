package com.community.messenger.common.entities.imp

open class Entity (
        final override var id: String = "") :
    com.community.messenger.common.entities.interfaces.IEntity {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Entity) return false
        return id == other.id
    }

    override fun compareTo(other: com.community.messenger.common.entities.interfaces.IEntity): Int = id.compareTo(other.id)

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Entity(id='$id')"
    }
}