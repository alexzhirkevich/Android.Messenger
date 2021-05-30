package com.alexz.messenger.app.data.providers.interfaces

import com.alexz.messenger.app.data.entities.IEntityCollection
import io.reactivex.Completable
import io.reactivex.Observable

interface EntityProvider<T> {

    /**
     * Creates observable for entity with given [id].
     * Entities, stored in additional collections, require [collectionID].
     * @return Observable state of requested model
     * */
    fun get(id : String, collectionID: String?=null) : Observable<T>

    /**
     * Creates observable for last [limit] entities in [collection].
     * If [limit] is -1, requests all entities in collection.
     * [limit] is 30 by default
     * * @return [Observable] state of requested model
     * */
    fun getAll(collection: IEntityCollection, limit:Int = 30) : Observable<List<T>>

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

    /**
     * Removes entity [id] (reference) from [collection].
     * @return [Completable] for action done.
     */
    fun remove(id : String, collection: IEntityCollection?=null) : Completable
}