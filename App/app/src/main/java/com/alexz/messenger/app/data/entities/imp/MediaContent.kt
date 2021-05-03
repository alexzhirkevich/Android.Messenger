package com.alexz.messenger.app.data.entities.imp

import android.os.Parcel
import android.os.Parcelable
import com.alexz.messenger.app.data.entities.interfaces.IMediaContent

open class MediaContent(override var type: Int = IMediaContent.IMAGE, override var url: String ="" ) : IMediaContent, Parcelable {

    constructor(parcel: Parcel) :
            this(type = parcel.readInt(), url = parcel.readString().orEmpty())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MediaContent) return false

        if (type != other.type) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result *= 31 + url.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<MediaContent> {
        override fun createFromParcel(parcel: Parcel): MediaContent {
            return MediaContent(parcel)
        }

        override fun newArray(size: Int): Array<MediaContent?> {
            return arrayOfNulls(size)
        }
    }
}