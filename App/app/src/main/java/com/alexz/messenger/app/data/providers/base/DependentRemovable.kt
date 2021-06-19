package com.alexz.messenger.app.data.providers.base

import com.alexz.firerecadapter.IEntity
import io.reactivex.Completable

interface DependentRemovable<Collection : IEntity> : Removable {
    /**
     * Removes entity [id] (reference) from [collection].
     * @return [Completable] for action done.
     */
    fun remove(id : String, collection: Collection) : Completable
}