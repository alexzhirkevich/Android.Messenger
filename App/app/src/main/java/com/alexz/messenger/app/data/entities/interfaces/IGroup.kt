package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IGroup  : IEntity, IListable, IChat {
    var creatorId: String
    var creationTime: Long
}