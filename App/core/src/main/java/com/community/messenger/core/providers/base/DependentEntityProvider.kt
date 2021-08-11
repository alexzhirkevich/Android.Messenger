package com.community.messenger.core.providers.base

import com.community.messenger.common.entities.interfaces.IEntity
import io.reactivex.Completable
import io.reactivex.Observable

interface DependentEntityProvider<T: IEntity,Collection: IEntity> : Provider {

    /**
     * Creates observable for entity with given [id].
     * Entities, stored in additional collections, require [collectionID].
     * @return Observable state of requested model
     * */
    fun get(id : String, collectionID: String) : Observable<out T>

    /**
     * Creates a new [entity].
     * @return [Completable] for action done.
     */
    fun create(entity : T) : Completable

    /**
     * Deletes [entity] from data source.
     * @return [Completable] for action done.
     */
    fun delete(entity : T) : Completable
}