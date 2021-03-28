package com.alexz.messenger.app.data.model.interfaces

interface IUser {
    var name: String
    var imageUri: String
    var lastOnline: Long
    var isOnline: Boolean
}