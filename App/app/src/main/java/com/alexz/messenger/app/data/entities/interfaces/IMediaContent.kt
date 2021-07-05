package com.alexz.messenger.app.data.entities.interfaces

import android.os.Parcelable
import com.alexz.firerecadapter.IEntity

interface IMediaContent : IEntity,Parcelable  {
    companion object {
        const val IMAGE = 0
        const val VIDEO = 1
    }

    var type: Int
    var url: String
}