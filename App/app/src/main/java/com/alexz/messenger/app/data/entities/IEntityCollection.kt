package com.alexz.messenger.app.data.entities

import com.alexz.firerecadapter.IEntity

interface IEntityCollection: IEntity, Collection<Class<out Any>>