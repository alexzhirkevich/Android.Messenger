package com.alexz.firerecadapter

open class Entity (
        final override var id: String = "") : IEntity {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Entity) return false
        return id == other.id
    }

    override fun compareTo(other: IEntity): Int = id.compareTo(other.id)

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Entity(id='$id')"
    }
}