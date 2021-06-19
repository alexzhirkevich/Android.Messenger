package com.alexz.messenger.app.data.providers.base

import io.reactivex.Completable

interface SingleRemovable : Removable {
    /**
     * Removes entity [id] (reference).
     * @return [Completable] for action done.
     */
    fun remove(id : String) : Completable
}