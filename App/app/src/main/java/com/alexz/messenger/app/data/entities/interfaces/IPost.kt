package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.imp.MediaContent

interface IPost  : IEntity {
    var channelId: String
    var creatorId: String
    var text: String
    var content: MutableList<MediaContent>
    var time: Long
}