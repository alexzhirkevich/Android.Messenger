package com.community.messenger.common.entities.interfaces

import com.community.messenger.common.entities.imp.MediaContent

interface IPost  : IEntity {
    var channelId: String
    var creatorId: String
    var text: String
    var content: MutableList<out MediaContent>
    var time: Long
}