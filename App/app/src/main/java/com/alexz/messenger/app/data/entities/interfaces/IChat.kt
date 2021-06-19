package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IChat  : IEntity, IMessageable, IUserContainer {
    var imageUri: String
    var name: String
    var creatorId: String
    var creationTime: Long
}