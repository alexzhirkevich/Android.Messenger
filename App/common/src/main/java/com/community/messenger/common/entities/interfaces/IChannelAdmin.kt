package com.community.messenger.common.entities.interfaces

interface IChannelAdmin  : IEntity {

    var canEdit : Boolean
    var canPost : Boolean
    var canDelete : Boolean
    var canBan : Boolean

}