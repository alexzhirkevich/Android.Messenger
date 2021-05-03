package com.alexz.firerecadapter

import io.reactivex.rxjava3.core.Single

interface ILocalProvider<Entity : IEntity> {

    val adapterCallback: AdapterCallback<Entity>

    fun load() : Single<List<Entity>>
}