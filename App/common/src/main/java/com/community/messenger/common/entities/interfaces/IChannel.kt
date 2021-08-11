package com.community.messenger.common.entities.interfaces

import android.os.Parcelable

interface IChannel : IEntity, IListable, Parcelable {
    var tag: String
    var description: String
    var lastPostId: String
    var lastPostTime : Long
    var creatorId: String
    var creationTime: Long
}