package com.community.messenger.common.entities.interfaces


interface IMessage  : IEntity {
    var chatId: String
    var senderId: String
    var text: String
    var time: Long
    var isPrivate: Boolean
}