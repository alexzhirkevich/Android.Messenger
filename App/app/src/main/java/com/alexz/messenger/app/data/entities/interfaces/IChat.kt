package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IChat : IEntity {
    var lastMessageId : String
    var lastMessageTime : Long
}