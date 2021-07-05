package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IEvent : IEntity, IListable {

    val creatorId : String
    val description : String
    val time : Long
    val address : String
    val isValid : Boolean
    val isEnded : Boolean
}