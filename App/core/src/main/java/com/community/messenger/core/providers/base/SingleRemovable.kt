package com.community.messenger.core.providers.base

import io.reactivex.Completable

interface SingleRemovable : Provider {
    /**
     * Removes entity [id] (reference).
     * @return [Completable] for action done.
     */
    fun remove(id : String) : Completable
}