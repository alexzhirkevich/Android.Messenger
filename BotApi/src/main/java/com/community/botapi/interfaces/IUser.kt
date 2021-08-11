package com.community.botapi.interfaces

import com.community.botapi.interfaces.IEntity

interface IUser  : IEntity {
    var name : String
    var imageUri : String
    var username : String
    var description : String
}