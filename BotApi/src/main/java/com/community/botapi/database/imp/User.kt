package com.community.botapi.database.imp

import com.community.botapi.interfaces.IUser

data class User protected constructor(
    override var id: String,
    override var name: String,
    override var imageUri: String,
    override var username: String,
    override var description: String
) : IUser {

}