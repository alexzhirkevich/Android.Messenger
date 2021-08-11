package com.community.messenger.core.providers.base

import com.community.messenger.common.entities.interfaces.IEntity
import io.reactivex.Completable

interface DependentRemovable<Collection : IEntity> : Provider {
    /**
     * Removes entity [id] (reference) from [collection].
     * @return [Completable] for action done.
     */
    fun remove(id : String, collection: Collection) : Completable
}