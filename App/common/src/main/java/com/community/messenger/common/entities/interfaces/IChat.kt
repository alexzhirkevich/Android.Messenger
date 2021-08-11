package com.community.messenger.common.entities.interfaces

interface IChat : IEntity {
    var lastMessageId : String
    var lastMessageTime : Long
}