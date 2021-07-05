package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IChannel : IEntity, IListable {
    var lastPostId: String
    var lastPostTime : Long
    var creatorId: String
    var creationTime: Long
}