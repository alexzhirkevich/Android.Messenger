package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IChannelAdmin  : IEntity {

    var canEdit : Boolean
    var canPost : Boolean
    var canDelete : Boolean
    var canBan : Boolean

}