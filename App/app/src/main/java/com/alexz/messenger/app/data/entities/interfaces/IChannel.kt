package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity
import com.alexz.messenger.app.data.entities.imp.ChannelAdmin

interface IChannel : IEntity{
    var name: String
    var imageUri: String
    var lastPostId: String
    var lastPostTime : Long
    var creatorId: String
    var creationTime: Long
    var admins: MutableMap<String, ChannelAdmin>
}