package com.alexz.messenger.app.data.model.interfaces

interface IMediaMessage<Content : IMediaContent> {
    var mediaContent: List<Content>
}