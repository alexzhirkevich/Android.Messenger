package com.community.messenger.core.providers.base

import com.community.messenger.common.entities.interfaces.IEntity
import io.reactivex.Observable

interface RangeEntityProvider<T: IEntity> : Provider {
    /**
     * Creates observable for last [limit] entities.
     * If [limit] is -1, requests all entities in collection.
     * [limit] is 30 by default
     * * @return [Observable] state of requested model
     * */
    fun getAll(limit:Int = 30) : Observable<Collection<T>>
}
