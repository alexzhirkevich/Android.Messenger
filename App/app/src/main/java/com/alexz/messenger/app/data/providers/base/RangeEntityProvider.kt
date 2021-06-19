package com.alexz.messenger.app.data.providers.base

import com.alexz.firerecadapter.IEntity
import io.reactivex.Observable

interface RangeEntityProvider<T: IEntity> : EntityProvider {
    /**
     * Creates observable for last [limit] entities.
     * If [limit] is -1, requests all entities in collection.
     * [limit] is 30 by default
     * * @return [Observable] state of requested model
     * */
    fun getAll(limit:Int = 30) : Observable<List<T>>
}
