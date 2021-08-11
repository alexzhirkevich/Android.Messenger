package com.community.messenger.common.entities.interfaces

interface IVoiceMessage  : IEntity {

    var voiceLen : Int
    var voiceUri : String
}