package com.alexz.messenger.app.data.providers.base

import com.alexz.firerecadapter.IEntity
import io.reactivex.Completable
import io.reactivex.Observable

interface SingleEntityProvider<T:IEntity> {

    /**
     * Creates observable for entity with given [id].
     * @return Observable state of requested model
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