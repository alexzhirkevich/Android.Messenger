package com.alexz.messenger.app.data.entities.dao

import com.alexz.firerecadapter.IEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface EntityDao<Entity : IEntity>  {

    fun get(id : String) : Single<Entity>

    fun add(entity: Entity): Completable

    fun delete(id : String): Completable

    fun contains(id : String) : Single<Boolean>
}