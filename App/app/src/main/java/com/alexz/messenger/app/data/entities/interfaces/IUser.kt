package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IUser  : IEntity {
    var name: String
    var imageUri: String
    var lastOnline: Long
    var isOnline: Boolean
}