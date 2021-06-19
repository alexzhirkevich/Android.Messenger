package com.alexz.messenger.app.data.providers.base

import com.alexz.firerecadapter.IEntity
import io.reactivex.Observable

interface DependentRangeEntityProvider<T: IEntity,Collection : IEntity> : EntityProvider {
    /**
     * Creates observable for last [limit] entities in [collection].
     * If [limit] is -1, requests all entities in collection.
     * [limit] is 30 by default
     * * @return [Observable] state of requested model
     * */
    fun getAll(collection: Collection, limit:Int = 30) : Observable<List<T>>
}