package com.community.messenger.core.providers.base

import com.community.messenger.common.entities.interfaces.IEntity
import io.reactivex.Completable
import io.reactivex.Observable

interface SingleEntityProvider<T :IEntity> : Provider {

    /**
     * Creates [Observable] for entity with given [id].
     * @return [Observable] state of requested entity
     * */
    fun get(id : String) : Observable<T>

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