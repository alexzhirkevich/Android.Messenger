package com.community.messenger.common.entities.interfaces

import android.os.Parcelable

interface IUser  : IContact, IListable, Parcelable {
    var username : String
    var description : String
    var creationTime : Long
    var lastOnline: Long
    var isOnline: Boolean
    var isBot : Boolean
}