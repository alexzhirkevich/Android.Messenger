package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IChat  : IEntity {
    var imageUri: String
    var name: String
    val lastMessageTime : Long
    val lastMessageId : String
    var isGroup: Boolean
    var creatorId: String
    var creationTime: Long
}