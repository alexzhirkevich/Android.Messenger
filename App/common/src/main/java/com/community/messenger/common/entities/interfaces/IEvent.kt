package com.community.messenger.common.entities.interfaces


interface IEvent : IEntity, IListable {

    var creatorId : String
    var description : String
    var time : Long
    var address : String
    var location : Pair<Float,Float>
    var isValid : Boolean
    var isEnded : Boolean
}