package com.alexz.messenger.app.data.model.interfaces

import com.alexz.messenger.app.data.model.imp.Message

interface IChat {
    var imageUri: String
    var name: String
    var lastMessage: Message?
    var isGroup: Boolean
    var creatorId: String
    var creationTime: Long
}