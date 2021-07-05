package com.alexz.messenger.app.data.entities.interfaces

import android.os.Parcelable
import com.alexz.firerecadapter.IEntity

interface IUser  : IListable, Parcelable,IEntity {
    var phone : String
    var username : String
    var description : String
    var creationTime : Long
    var lastOnline: Long
    var isOnline: Boolean
}