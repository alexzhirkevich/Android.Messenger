package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.messenger.app.data.entities.IEntityCollection

interface IChannel : IEntityCollection {
    var name: String
    var imageUri: String
    var lastPostId: String
    var lastPostTime : Long
    var creatorId: String
    var creationTime: Long
}