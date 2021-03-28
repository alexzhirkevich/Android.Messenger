package com.alexz.messenger.app.data.model.interfaces

interface IMessage {
    var chatId: String
    var senderId: String
    var senderName: String
    var senderPhotoUrl: String
    var text: String
    var time: Long
    var isPrivate: Boolean
}