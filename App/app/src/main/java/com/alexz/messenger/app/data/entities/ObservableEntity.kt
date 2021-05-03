package com.alexz.messenger.app.data.entities

import com.alexz.firerecadapter.IEntity

interface ObservableEntity<Entity : IEntity> {
    val state : Entity?
}