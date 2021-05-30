package com.alexz.messenger.app.data.entities

import androidx.room.Ignore
import com.alexz.firerecadapter.Entity

//@androidx.room.Entity(inheritSuperIndices = true)
open class EntityCollection(id : String, @Ignore private val entities: Collection<Class<out Any>>)
    : Entity(id), IEntityCollection, Collection<Class<out Any>> by entities{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityCollection) return false
        if (!super.equals(other)) return false

        if (entities != other.entities) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + entities.hashCode()
        return result
    }

    override fun toString(): String {
        return "EntityCollection(entities=$entities)"
    }
}