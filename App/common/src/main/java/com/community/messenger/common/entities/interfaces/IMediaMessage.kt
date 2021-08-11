package com.community.messenger.common.entities.interfaces


interface IMediaMessage<Content : IMediaContent> : IEntity {
    var mediaContent: List<Content>
}