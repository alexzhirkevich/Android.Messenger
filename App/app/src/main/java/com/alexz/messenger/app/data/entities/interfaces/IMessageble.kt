package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IMessageable : IEntity {
    var lastMessageId : String
    var lastMessageTime : Long
}