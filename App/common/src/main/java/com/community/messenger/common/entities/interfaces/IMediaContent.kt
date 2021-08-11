package com.community.messenger.common.entities.interfaces

import android.os.Parcelable

interface IMediaContent : IEntity,Parcelable  {
    companion object {
        const val IMAGE = 0
        const val VIDEO = 1
    }

    var type: Int
    var url: String
}