package com.alexz.messenger.app.data.model.imp

import com.alexz.messenger.app.data.model.interfaces.IMediaMessage

class MediaMessage(chatId: String = "") : Message(chatId),IMediaMessage<MediaContent> {
    override var mediaContent: List<MediaContent> = ArrayList()
}