package com.alexz.messenger.app.data.entities.interfaces

import com.alexz.firerecadapter.IEntity

interface IVoiceMessage  : IEntity {

    var voiceLen : Int
    var voiceUri : String
}