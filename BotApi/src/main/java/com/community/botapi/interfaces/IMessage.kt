package com.community.botapi.interfaces

interface IMessage  : IEntity {
    val chatId : String
    val senderId: String
    val text: String
    val time: Long
}