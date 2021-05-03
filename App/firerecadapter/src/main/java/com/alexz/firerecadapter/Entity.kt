package com.alexz.firerecadapter

import androidx.room.ColumnInfo
import androidx.room.Index
import androidx.room.PrimaryKey

@androidx.room.Entity(indices = [Index(value = ["id"],unique = true)])
open class Entity (
        @PrimaryKey()
        @ColumnInfo(name = "id")
        final override var id: String = "") : IEntity {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Entity) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Entity(id='$id')"
    }
}