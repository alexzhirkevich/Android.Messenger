package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IMediaMessage<Content : IMediaContent> : IEntity {
    var mediaContent: List<Content>
}