package com.community.messenger.core.providers.base

import com.community.messenger.common.entities.interfaces.IEntity
import io.reactivex.Observable

interface DependentRangeEntityProvider<T: IEntity,Collection : IEntity> : Provider {
    /**
     * Creates observable for last [limit] entities in [collection].
     * If [limit] is -1, requests all entities in collection.
     * [limit] is 30 by default
     * * @return [Observable] state of requested model
     * */
    fun getAll(collection: Collection, limit:Int = 30) : Observable<kotlin.collections.Collection<T>>
}