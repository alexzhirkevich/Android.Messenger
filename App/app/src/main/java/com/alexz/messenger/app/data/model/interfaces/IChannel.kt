package com.alexz.messenger.app.data.model.interfaces

interface IChannel {
    var imageUri: String?
    var name: String?
    var lastPost: IPost?
    var creatorId: String?
    var creationTime: Long?
}