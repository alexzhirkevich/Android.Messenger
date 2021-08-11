package com.community.messenger.common.entities.interfaces


interface IGroup  : IEntity, IListable, IChat {
    var creatorId: String
    var creationTime: Long
}