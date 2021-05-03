package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IMessage  : IEntity {
    var chatId: String
    var senderId: String
    var text: String
    var time: Long
    var isPrivate: Boolean
}