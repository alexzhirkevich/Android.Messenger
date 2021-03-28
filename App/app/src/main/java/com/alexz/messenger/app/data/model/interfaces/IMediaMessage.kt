package com.alexz.messenger.app.data.model.interfaces

interface IMediaMessage<Content : IMediaContent> {
    var mediaContent: List<Content>

    companion object {
        const val MEDIA_CONTENT = "mediaContent"
    }
}