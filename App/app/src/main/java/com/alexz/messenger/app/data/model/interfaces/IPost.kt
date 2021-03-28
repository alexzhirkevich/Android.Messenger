package com.alexz.messenger.app.data.model.interfaces

interface IPost {
    var channelId: String
    var text: String
    var content: List<String>
    var time: Long
}